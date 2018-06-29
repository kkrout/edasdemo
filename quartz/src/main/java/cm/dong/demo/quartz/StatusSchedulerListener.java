package cm.dong.demo.quartz;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusSchedulerListener implements SchedulerListener
{

    private static Logger log = LoggerFactory.getLogger(StatusSchedulerListener.class);

    private RedissonClient redissonClient;
    private Scheduler scheduler;

    @Override
    public void jobScheduled(Trigger trigger)
    {
        TriggerKey triggetKey = trigger.getKey();
        String name = triggetKey.getName();
        String group = triggetKey.getGroup();
        System.out.println("job=[" + name + "," + group + "]被启动初始化。。。");
        updateState(triggetKey);
        log.info("job被启动初始化。。。");
    }

    public void updateState(TriggerKey triggetKey)
    {
        TriggerState triggerState;
        String name = triggetKey.getName();
        String group = triggetKey.getGroup();
        try
        {
            triggerState = scheduler.getTriggerState(triggetKey);
            RMap<Object, Object> map = redissonClient.getMap("jobStatus");
            map.put(group + name, triggerState.toString());
        }
        catch (SchedulerException e)
        {
            log.error("获取状态错误", e);
            // 杀掉job任务
            jobdelete(name, group);
        }
    }

    public void jobdelete(String name, String group)
    {
        try
        {
            scheduler.pauseTrigger(TriggerKey.triggerKey(name, group));
            scheduler.unscheduleJob(TriggerKey.triggerKey(name, group));
            scheduler.deleteJob(JobKey.jobKey(name, group));
        }
        catch (SchedulerException e)
        {
            log.error("JOB监听，杀掉job失败", e);
        }
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void triggerFinalized(Trigger trigger)
    {
        // TODO Auto-generated method stub
        updateState(trigger.getKey());
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey)
    {
        updateState(triggerKey);
    }

    @Override
    public void triggersPaused(String triggerGroup)
    {

    }

    @Override
    public void triggerResumed(TriggerKey triggerKey)
    {
        updateState(triggerKey);
    }

    @Override
    public void triggersResumed(String triggerGroup)
    {

    }

    public void updateJobState(TriggerKey triggetKey)
    {
        TriggerState triggerState;
        String name = triggetKey.getName();
        String group = triggetKey.getGroup();
        try
        {
            triggerState = scheduler.getTriggerState(triggetKey);
            RMap<Object, Object> map = redissonClient.getMap("jobStatus");
            map.put(group + name, triggerState.toString());
        }
        catch (SchedulerException e)
        {
            log.error("获取状态错误", e);
            // 杀掉job任务
            jobdelete(name, group);
        }
    }

    @Override
    public void jobAdded(JobDetail jobDetail)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void jobDeleted(JobKey jobKey)
    {
        String group = jobKey.getGroup();
        String name = jobKey.getName();
        RMap<Object, Object> map = redissonClient.getMap("jobStatus");
        map.remove(group + name);
    }

    @Override
    public void jobPaused(JobKey jobKey)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void jobsPaused(String jobGroup)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void jobResumed(JobKey jobKey)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void jobsResumed(String jobGroup)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void schedulerError(String msg, SchedulerException cause)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void schedulerInStandbyMode()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void schedulerStarted()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void schedulerStarting()
    {
        // TODO Auto-generated method stub
        log.info("scheduler初始化....");
        redissonClient = SpringUtils.getBean("redissonClient", RedissonClient.class);
        scheduler = SpringUtils.getBean("Scheduler", Scheduler.class);
    }

    @Override
    public void schedulerShutdown()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void schedulerShuttingdown()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void schedulingDataCleared()
    {
        // TODO Auto-generated method stub

    }

}
