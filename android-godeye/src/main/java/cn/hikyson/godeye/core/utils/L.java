package cn.hikyson.godeye.core.utils;

/**
 * Description: <日志打印> Author: hui.zhao Date: 2016/7/13 Copyright: Ctrip
 */
public class L {
    public static final String DEFAULT_TAG = "AndroidGodEye";

    public interface LogProxy {
        public void d(String msg);

        public void e(String msg);

        void onRuntimeException(RuntimeException e);
    }

    private static LogProxy sLogProxy;

    public static void setProxy(LogProxy logProxy) {
        sLogProxy = logProxy;
    }

    public static void d(Object msg) {
        if (sLogProxy != null) {
            sLogProxy.d(o2String(msg));
        }
    }

    public static void d(String format, Object... msgs) {
        if (sLogProxy != null) {
            String[] args = new String[msgs.length];
            for (int i = 0; i < msgs.length; i++) {
                args[i] = o2String(msgs[i]);
            }
            sLogProxy.d(String.format(format, args));
        }
    }

    public static void e(Object msg) {
        if (sLogProxy != null) {
            sLogProxy.e(o2String(msg));
        }
    }

    public static void onRuntimeException(RuntimeException e) {
        if (sLogProxy != null) {
            sLogProxy.onRuntimeException(e);
        }
    }

    private static String o2String(Object o) {
        if (o instanceof String) {
            return (String) o;
        }
//        if (o instanceof GodEyeConfig) {
//            return JsonUtil.toJson(o);
//        }
        return String.valueOf(o);
    }
}
