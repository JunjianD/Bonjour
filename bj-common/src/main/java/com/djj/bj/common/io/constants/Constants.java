package com.djj.bj.common.io.constants;

/**
 * 常量类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.constants
 * @className Constants
 * @date 2025/6/10 22:09
 */
public class Constants {
    /**
     * 用户ID分隔符
     */
    public static final String USER_ID_SPLIT = ",";

    /**
     * Redis Key的分隔符
     */
    public static final String REDIS_KEY_SPLIT = ":";

    /**
     * 发送消息的Key
     */
    public static final String MESSAGE_KEY_SPLIT = "_";

    /**
     * 在线状态过期时间，默认是600秒，也就是10分钟
     */
    public static final long ONLINE_TIMEOUT_SECONDS = 600;

    /**
     * 消息允许撤回时间，默认300秒，也就是5分钟
     */
    public static final long ALLOW_RECALL_SECOND = 300;

    /**
     * server最大id,从0开始递增
     */
    public final static String MAX_SERVER_ID = "max_server_id";

    /**
     * 用户ID所连接的server的ID
     */
    public final static String USER_SERVER_ID = "user:server_id";

    /**
     * 消息的key
     */
    public static final String MSG_KEY = "message";

    /**
     * 未读私聊消息队列
     */
    public final static String MESSAGE_PRIVATE_QUEUE = "message_private";

    /**
     * 未读群聊消息队列
     */
    public final static String MESSAGE_GROUP_QUEUE = "message_group";

    /**
     * 未读私聊消息空队列
     */
    public final static String MESSAGE_PRIVATE_NULL_QUEUE = "message_private_null";

    /**
     * 未读群聊消息空队列
     */
    public final static String MESSAGE_GROUP_NULL_QUEUE = "message_group_null";

    /**
     * 监听私聊消息消费分组
     */
    public final static String MESSAGE_PRIVATE_CONSUMER_GROUP = "message_private_consumer_group";

    /**
     * 监听群聊消息消费分组
     */
    public final static String MESSAGE_GROUP_CONSUMER_GROUP = "message_group_consumer_group";

    /**
     * 私聊消息结果队列
     */
    public final static String RESULT_MESSAGE_PRIVATE_QUEUE = "result_message_private";

    /**
     * 群聊消息结果队列
     */
    public final static String RESULT_MESSAGE_GROUP_QUEUE = "result_message_group";

    /**
     * 私聊消息结果消费分组
     */
    public final static String RESULT_MESSAGE_PRIVATE_CONSUMER_GROUP = "result_message_private_consumer_group";

    /**
     * 群聊消息结果消费分组
     */
    public final static String RESULT_MESSAGE_GROUP_CONSUMER_GROUP = "result_message_group_consumer_group";

    /**
     * 用户ID
     */
    public static final String USER_ID = "USER_ID";

    /**
     * 终端类型
     */
    public static final String TERMINAL_TYPE = "TERMINAL_TYPE";

    /**
     * 心跳次数
     */
    public static final String HEARTBEAT_COUNTS = "HEARTBEAT_COUNTS";

    /**
     * 解码时 读取最小字节数
     */
    public static final int READ_MINIMUM_BYTES = 4;

    /**
     * redis
     */
    public static final String DISTRIBUTED_CACHE_REDIS_SERVICE_KEY = "distributed_cache_redis_service";

    /**
     * ServerGroup Bean
     */
    public static final String SERVER_GROUP_BEAN_NAME = "serverGroup";

    /**
     * 最大图片大小
     */
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * 最大文件大小
     */
    public static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB

    /**
     * 群聊最大人数
     */
    public static final int MAX_GROUP_MEMBER_COUNT = 500; // 最大群聊人数限制为500人

    /**
     * 已读群聊消息位置
     */
    public static final String GROUP_MESSAGE_READ_POSITION = "group_message_read:position";

    /**
     * webrtc 会话信息
     */
    public static final String WEBRTC_SESSION_INFO = "webrtc:session:info";

    /**
     * 缓存前缀
     */
    public static final String CACHE_PREFIX = "cache:";

    /**
     * 缓存是否好友
     */
    public static final String CACHE_IS_FRIEND = CACHE_PREFIX + "is_friend:";

    /**
     * 缓存群聊信息
     */
    public static final String CACHE_GROUP_INFO = CACHE_PREFIX + "group_info:";

    /**
     * 缓存群聊成员ID
     */
    public static final String CACHE_GROUP_MEMBER_ID = CACHE_PREFIX + "group_member_ids:";


}
