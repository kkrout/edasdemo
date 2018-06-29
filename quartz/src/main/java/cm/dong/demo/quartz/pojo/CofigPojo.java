package cm.dong.demo.quartz.pojo;

public class CofigPojo
{

    private String id;
    private String group;
    private String name;
    private String cron;
    private String allowConcurrent;
    private String targetClass;
    private String targetMethod;
    private String state;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCron()
    {
        return cron;
    }

    public void setCron(String cron)
    {
        this.cron = cron;
    }

    public String getAllowConcurrent()
    {
        return allowConcurrent;
    }

    public void setAllowConcurrent(String allowConcurrent)
    {
        this.allowConcurrent = allowConcurrent;
    }

    public String getTargetClass()
    {
        return targetClass;
    }

    public void setTargetClass(String targetClass)
    {
        this.targetClass = targetClass;
    }

    public String getTargetMethod()
    {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod)
    {
        this.targetMethod = targetMethod;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

}
