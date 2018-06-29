package cm.dong.demo.quartz;

import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import cm.dong.demo.quartz.job.BaseJob;
import cm.dong.demo.quartz.pojo.CofigPojo;

@RestController
@RequestMapping(value = "/job")
public class JobController
{
    // @Autowired
    // private IJobAndTriggerService iJobAndTriggerService;

    // 加入Qulifier注解，通过名称注入bean
    @Autowired
    @Qualifier("Scheduler")
    private Scheduler scheduler;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private RedissonClient redissonClient;

    private static Logger log = LoggerFactory.getLogger(JobController.class);

    private String state;

    @PostMapping(value = "/addjob", consumes = "application/json")
    public String addjob(@RequestBody CofigPojo cp) throws Exception
    {
        RMap<Object, Object> jobMap = redissonClient.getMap("jobList");
        String key = cp.getGroup() + cp.getTargetClass();
        JSONObject result = new JSONObject();

        if (!jobMap.containsKey(key))
        {
            jobMap.put(key, jsonMapper.writeValueAsString(cp));
            result.put("status", "OK");
        }
        else
        {
            result.put("result", "已存在记录");
            result.put("status", "error");
        }
        return result.toString();
    }

    @GetMapping(value = "/jobClassList")
    public String jobClassList() throws Exception
    {
        List<Class<?>> listClass = ClassUtil.getAllClassByInterface(BaseJob.class);
        return jsonMapper.writeValueAsString(listClass);
    }

    @GetMapping(value = "/list")
    public String listjob() throws Exception
    {
        RMap<Object, Object> jobMap = redissonClient.getMap("jobList");
        // RMap<Object, Object> jobStatusMap = redissonClient.getMap("jobStatus");
        Collection<Object> values = jobMap.values();
        JSONArray result = new JSONArray();
        for (Object object : values)
        {
            String json = (String) object;
            JSONObject cp = new JSONObject(json);
            String group = cp.getString("group");
            String targetClass = cp.getString("targetClass");
            state = getState(targetClass, group);
            // state = (String) jobStatusMap.get(group + targetClass);
            cp.put("state", state);
            result.put(cp);
        }
        return result.toString();
    }

    @GetMapping(value = "/state")
    public String getState(@RequestParam(value = "jobClassName") String jobClassName,
            @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception
    {
        TriggerState triggerState = scheduler.getTriggerState(new TriggerKey(jobClassName, jobGroupName));
        if (triggerState == null)
        {
            return "NONE";
        }

        return triggerState.toString();
    }

    @PostMapping(value = "/runjob")
    public String runjob(@RequestParam(value = "targetClass") String targetClass,
            @RequestParam(value = "group") String group) throws Exception
    {

        RMap<Object, Object> jobMap = redissonClient.getMap("jobList");
        String key = group + targetClass;
        JSONObject result = new JSONObject();
        try
        {

            String jobJson = (String) jobMap.get(key);
            JSONObject json = new JSONObject(jobJson);
            String cronExpression = json.getString("cron");

            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(getClass(targetClass).getClass()).withIdentity(targetClass, group)
                    .build();

            // 表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(targetClass, group)
                    .withSchedule(scheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
            result.put("status", "OK");
        }
        catch (ObjectAlreadyExistsException e)
        {
            result.put("status", "ERROR");
            result.put("error", "定时任务已经存在");
            log.error("定时任务已经存在", e);
        }
        catch (SchedulerException e)
        {
            result.put("status", "ERROR");
            result.put("error", "创建定时任务失败");
            log.error("创建定时任务失败", e);
        }
        catch (Exception e)
        {
            result.put("status", "ERROR");
            result.put("error", "其他异常");
            log.error("其他异常", e);
        }
        // finally
        // {
        // if (lock != null)
        // {
        // lock.unlock();
        // }
        // }

        // JobKey jobKey = JobKey.jobKey(targetClass, group);
        // if (jobKey != null)
        // {
        // JobDetail job = scheduler.getJobDetail(jobKey);
        // if (job != null)
        // {
        // TriggerState triggerState = scheduler.getTriggerState(new TriggerKey(targetClass,
        // group));
        // if (triggerState != null)
        // {
        // result.put("jobStatue", triggerState.toString());
        // }
        // }
        //
        // }
        return result.toString();
    }

    @PostMapping(value = "/pausejob")
    public String pausejob(@RequestParam(value = "targetClass") String targetClass,
            @RequestParam(value = "group") String group) throws Exception
    {
        JSONObject result = new JSONObject();

        try
        {
            // scheduler.pauseJob(JobKey.jobKey(targetClass, group));
            scheduler.pauseTrigger(TriggerKey.triggerKey(targetClass, group));
        }
        catch (Exception e)
        {
            result.put("status", "ERROR");
            result.put("error", "其他异常");
            log.error("其他异常", e);
        }
        return result.toString();
    }

    @PostMapping(value = "/resumejob")
    public String resumejob(@RequestParam(value = "targetClass") String targetClass,
            @RequestParam(value = "group") String group) throws Exception
    {
        JSONObject result = new JSONObject();
        try
        {
            // scheduler.resumeJob(JobKey.jobKey(targetClass, group));
            scheduler.resumeTrigger(TriggerKey.triggerKey(targetClass, group));

            result.put("status", "OK");
        }
        catch (Exception e)
        {
            result.put("status", "ERROR");
            result.put("error", "其他异常");
            log.error("其他异常", e);
        }
        return result.toString();
    }

    @PostMapping(value = "/reschedulejob")
    public String rescheduleJob(@RequestBody CofigPojo cp) throws Exception
    {
        return jobreschedule(cp.getTargetClass(), cp.getGroup(), cp.getCron());
    }

    public String jobreschedule(String jobClassName, String jobGroupName, String cronExpression) throws Exception
    {

        JSONObject result = new JSONObject();
        try
        {
            RMap<Object, Object> jobMap = redissonClient.getMap("jobList");

            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            if (trigger != null)
            {

                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

                // 按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);

            }

            // 更新缓存
            CofigPojo object = jsonMapper.readValue((String) jobMap.get(jobGroupName + jobClassName), CofigPojo.class);
            object.setCron(cronExpression);

            jobMap.put(jobGroupName + jobClassName, jsonMapper.writeValueAsString(object));

            result.put("status", "OK");
        }
        catch (Exception e)
        {
            result.put("status", "ERROR");
            result.put("error", "更新定时任务失败:" + e.getMessage());
            log.error("更新定时任务失败", e);
        }
        return result.toString();
    }

    @PostMapping(value = "/deletejob")
    public String deletejob(@RequestParam(value = "targetClass") String jobClassName,
            @RequestParam(value = "group") String jobGroupName) throws Exception
    {
        JSONObject result = new JSONObject();
        try
        {
            RMap<Object, Object> jobMap = redissonClient.getMap("jobList");
            RMap<Object, Object> jobStatusMap = redissonClient.getMap("jobStatus");

            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            if (scheduler.checkExists(triggerKey))
            {
                scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
                scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
                scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
            }
            jobMap.remove(jobGroupName + jobClassName);
            jobStatusMap.remove(jobGroupName + jobClassName);
        }
        catch (Exception e)
        {
            result.put("status", "ERROR");
            result.put("error", "删除定时任务失败");
            log.error("删除定时任务失败", e);
        }
        return result.toString();
    }

    @PostMapping(value = "/stopjob")
    public String stopjob(@RequestParam(value = "targetClass") String jobClassName,
            @RequestParam(value = "group") String jobGroupName) throws Exception
    {
        JSONObject result = new JSONObject();
        try
        {
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
        }
        catch (Exception e)
        {
            result.put("status", "ERROR");
            result.put("error", "停止定时任务失败");
            log.error("停止定时任务失败", e);
        }
        return result.toString();
    }

    public void jobdelete(String jobClassName, String jobGroupName) throws Exception
    {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
        scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
    }

    public static BaseJob getClass(String classname) throws Exception
    {
        Class<?> class1 = Class.forName(classname);
        return (BaseJob) class1.newInstance();
    }

}
