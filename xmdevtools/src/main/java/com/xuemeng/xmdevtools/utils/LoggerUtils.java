package com.xuemeng.xmdevtools.utils;

import android.util.Log;

import java.util.Hashtable;

/**
 * 调试日志类
 * <p>
 * 0.使用一个标签来标记当前的APP
 * 1.支持多用户打印Log（在APP比较大，文件比较多，每人负责的模块不同时，可以使用自己的log来打印，这样看log的时候可以快
 * 速筛选出当前APP中你所设置的Log，比如只看leon的log就可以在Eclipse的filter里面输入Tony，这样显示的就都是你的log了）
 * 2.显示当前的线程名
 * 3.显示当前的Java文件与打印log的行号，便于快速定位到源文件
 * 4.显示当前是在那个方法体里面
 * 5.最后显示你设置打印出来的信息
 */
public class LoggerUtils {

    // 日志开关 转测true 正式 false
    public final static boolean logFlag = true;

    public final static String tag = "[XM-DEVTools]";
    private final static int logLevel = Log.VERBOSE;
    private static Hashtable<String, LoggerUtils> sLoggerTable = new Hashtable<String, LoggerUtils>();
    private String mClassName;

    private static LoggerUtils defaltLog;
    private static LoggerUtils flog;
    private static final String FAN = "@xuemeng@";


    private LoggerUtils(String name) {
        mClassName = name;
    }

    /**
     * 判断是否开启debug
     *
     * @return
     */
    public static boolean isDebug() {
        return logFlag;
    }

    /**
     * @param className
     * @return
     */
    @SuppressWarnings("unused")
    public static LoggerUtils getLogger(String className) {
        LoggerUtils classBaseXiuLogger = (LoggerUtils) sLoggerTable.get(className);
        if (classBaseXiuLogger == null) {
            classBaseXiuLogger = new LoggerUtils(className);
            sLoggerTable.put(className, classBaseXiuLogger);
        }
        return classBaseXiuLogger;
    }


    /**
     * 缺省名字的日志
     *
     * @return
     */
    public static LoggerUtils Log() {
        if (defaltLog == null) {
            defaltLog = new LoggerUtils("");
        }
        return defaltLog;
    }

    /**
     * 樊广军
     *
     * @return
     */
    public static LoggerUtils fLog() {
        if (flog == null) {
            flog = new LoggerUtils(FAN);
        }
        return flog;
    }


    /**
     * 获取当前函数名称
     *
     * @return
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return mClassName + "[ " + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + "() ]";
        }
        return null;
    }

    /**
     * 无参数日志 可以标记代码作用域是否执行了.
     */
    public void i() {
        i("");
    }

    /**
     * The Log Level:i
     *
     * @param str
     */
    public void i(Object str) {
        if (logFlag) {
            if (logLevel <= Log.INFO) {
                String name = getFunctionName();
                if (name != null) {
                    Log.i(tag, name + " - " + str);
                } else {
                    Log.i(tag, str.toString());
                }
            }
        }

    }

    /**
     * 无参数日志 可以标记代码作用域是否执行了.
     */
    public void d() {
        d("");
    }

    /**
     * The Log Level:d
     *
     * @param str
     */
    public void d(Object str) {
        if (logFlag) {
            str = str == null ? "obj is null!!" : str;
            if (logLevel <= Log.DEBUG) {
                String name = getFunctionName();
                if (name != null) {
                    Log.d(tag, name + " - " + str);
                } else {
                    Log.d(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:V
     *
     * @param str
     */
    public void v(Object str) {
        if (logFlag) {
            if (logLevel <= Log.VERBOSE) {
                String name = getFunctionName();
                if (name != null) {
                    Log.v(tag, name + " - " + str);
                } else {
                    Log.v(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:w
     *
     * @param str
     */
    public void w(Object str) {
        if (logFlag) {
            if (logLevel <= Log.WARN) {
                String name = getFunctionName();
                if (name != null) {
                    Log.w(tag, name + " - " + str);
                } else {
                    Log.w(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param str
     */
    public void e(Object str) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                String name = getFunctionName();
                if (name != null) {
                    Log.e(tag, name + " - " + str);
                } else {
                    Log.e(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param ex
     */
    public void e(Exception ex) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                Log.e(tag, "error", ex);
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param log
     * @param tr
     */
    public void e(String log, Throwable tr) {
        if (logFlag) {
            String line = getFunctionName();
            Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}"
                    + "[" + mClassName + line + ":] " + log + "\n", tr);
        }
    }
}
