package Util;

public class SnowflakeIdWorker {
    // 起始时间戳 (2010-11-04 01:42:54)
    private final long twepoch = 1288834974657L;

    // 机器ID所占的位数
    private final long workerIdBits = 5L;

    // 数据中心ID所占的位数
    private final long datacenterIdBits = 5L;

    // 支持的最大机器ID，结果是31 (0b11111)
    private final long maxWorkerId = ~(-1L << workerIdBits);

    // 支持的最大数据中心ID，结果是31 (0b11111)
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);

    // 序列号所占的位数
    private final long sequenceBits = 12L;

    // 机器ID向左移12位
    private final long workerIdShift = sequenceBits;

    // 数据中心ID向左移17位
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    // 时间戳向左移22位
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 生成序列的掩码 (0b111111111111 = 0xfff = 4095)
    private final long sequenceMask = ~(-1L << sequenceBits);

    // 工作机器ID (0~31)
    private long workerId;

    // 数据中心ID (0~31)
    private long datacenterId;

    // 序列号 (0~4095)
    private long sequence = 0L;

    // 上一次生成ID的时间戳
    private long lastTimestamp = -1L;

    // 构造函数
    public SnowflakeIdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // 获取下一个ID (该方法应为线程安全的)
    public synchronized long nextId() {
        // 获取当前时间戳
        long timestamp = timeGen();

        // 如果当前时间小于上一次生成ID的时间戳，说明系统时钟回退，抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 溢出处理
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
            // 时间戳改变，毫秒内序列重置
        } else {
            sequence = 0L;
        }

        // 上次生成ID的时间戳
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    // 阻塞到下一个毫秒，直到获得新的时间戳
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    // 获取当前时间戳
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    // 以上代码实现了Snowflake算法，这是一种分布式系统中生成唯一ID的方法。通过组合时间戳、数据中心ID、机器ID和序列号，我们可以保证生成的ID是全局唯一且趋势递增的。
    // 这种ID生成策略在分布式系统中非常有用，例如在分布式数据库和消息队列等场景下。

}