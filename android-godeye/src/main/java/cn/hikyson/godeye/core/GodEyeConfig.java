package cn.hikyson.godeye.core;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import cn.hikyson.godeye.core.internal.modules.battery.BatteryContext;
import cn.hikyson.godeye.core.internal.modules.cpu.CpuContext;
import cn.hikyson.godeye.core.internal.modules.crash.CrashFileProvider;
import cn.hikyson.godeye.core.internal.modules.crash.CrashInfo;
import cn.hikyson.godeye.core.internal.modules.crash.CrashProvider;
import cn.hikyson.godeye.core.internal.modules.fps.FpsContext;
import cn.hikyson.godeye.core.internal.modules.leakdetector.DefaultLeakRefInfoProvider;
import cn.hikyson.godeye.core.internal.modules.leakdetector.LeakContext;
import cn.hikyson.godeye.core.internal.modules.leakdetector.LeakRefInfoProvider;
import cn.hikyson.godeye.core.internal.modules.memory.HeapContext;
import cn.hikyson.godeye.core.internal.modules.memory.PssContext;
import cn.hikyson.godeye.core.internal.modules.memory.RamContext;
import cn.hikyson.godeye.core.internal.modules.methodcanary.MethodCanaryContext;
import cn.hikyson.godeye.core.internal.modules.network.NetworkContext;
import cn.hikyson.godeye.core.internal.modules.pageload.DefaultPageInfoProvider;
import cn.hikyson.godeye.core.internal.modules.pageload.PageInfoProvider;
import cn.hikyson.godeye.core.internal.modules.pageload.PageloadContext;
import cn.hikyson.godeye.core.internal.modules.sm.SmContext;
import cn.hikyson.godeye.core.internal.modules.startup.StartupContext;
import cn.hikyson.godeye.core.internal.modules.thread.ExcludeSystemThreadFilter;
import cn.hikyson.godeye.core.internal.modules.thread.ThreadContext;
import cn.hikyson.godeye.core.internal.modules.thread.ThreadFilter;
import cn.hikyson.godeye.core.internal.modules.traffic.TrafficContext;
import cn.hikyson.godeye.core.utils.IoUtil;
import cn.hikyson.godeye.core.utils.L;

/**
 * core config/module config
 */
public class GodEyeConfig implements Serializable {

    public static GodEyeConfigBuilder defaultConfigBuilder() {
        GodEyeConfigBuilder builder = GodEyeConfigBuilder.godEyeConfig();
        builder.withCpuConfig(new CpuConfig());
        builder.withBatteryConfig(new BatteryConfig());
        builder.withFpsConfig(new FpsConfig());
        builder.withLeakConfig(new LeakConfig());
        builder.withHeapConfig(new HeapConfig());
        builder.withPssConfig(new PssConfig());
        builder.withRamConfig(new RamConfig());
        builder.withNetworkConfig(new NetworkConfig());
        builder.withSmConfig(new SmConfig());
        builder.withStartupConfig(new StartupConfig());
        builder.withTrafficConfig(new TrafficConfig());
        builder.withCrashConfig(new CrashConfig());
        builder.withThreadConfig(new ThreadConfig());
        builder.withPageloadConfig(new PageloadConfig());
        builder.withMethodCanaryConfig(new MethodCanaryConfig());
        return builder;
    }

    public static GodEyeConfig defaultConfig() {
        return defaultConfigBuilder().build();
    }

    public static GodEyeConfig fromInputStream(InputStream is) {
        GodEyeConfigBuilder builder = GodEyeConfigBuilder.godEyeConfig();
        try {
            if (is == null) {
                throw new IllegalStateException("GodEyeConfig fromInputStream InputStream is null.");
            }
            Element root = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(is).getDocumentElement();
            // cpu
            Element element = getFirstElementByTagInRoot(root, "cpu");
            if (element != null) {
                final String intervalMillisString = element.getAttribute("intervalMillis");
                final String sampleMillisString = element.getAttribute("sampleMillis");
                CpuConfig cpuConfig = new CpuConfig();
                if (!TextUtils.isEmpty(intervalMillisString)) {
                    cpuConfig.intervalMillis = Long.parseLong(intervalMillisString);
                }
                if (!TextUtils.isEmpty(intervalMillisString)) {
                    cpuConfig.sampleMillis = Long.parseLong(sampleMillisString);
                }
                builder.withCpuConfig(cpuConfig);
            }
            // battery
            element = getFirstElementByTagInRoot(root, "battery");
            if (element != null) {
                BatteryConfig batteryConfig = new BatteryConfig();
                builder.withBatteryConfig(batteryConfig);
            }
            // fps
            element = getFirstElementByTagInRoot(root, "fps");
            if (element != null) {
                final String intervalMillisString = element.getAttribute("intervalMillis");
                FpsConfig fpsConfig = new FpsConfig();
                if (!TextUtils.isEmpty(intervalMillisString)) {
                    fpsConfig.intervalMillis = Long.parseLong(intervalMillisString);
                }
                builder.withFpsConfig(fpsConfig);
            }
            // leak
            element = getFirstElementByTagInRoot(root, "leakMemory");
            if (element != null) {
                final String debug = element.getAttribute("debug");
                final String debugNotification = element.getAttribute("debugNotification");
                final String leakRefInfoProvider = element.getAttribute("leakRefInfoProvider");
                LeakConfig leakConfig = new LeakConfig();
                if (!TextUtils.isEmpty(debug)) {
                    leakConfig.debug = Boolean.parseBoolean(debug);
                } else {
                    leakConfig.debug = true;
                }
                if (!TextUtils.isEmpty(debugNotification)) {
                    leakConfig.debugNotification = Boolean.parseBoolean(debugNotification);
                }
                if (!TextUtils.isEmpty(leakRefInfoProvider)) {
                    leakConfig.leakRefInfoProvider = (LeakRefInfoProvider) Class.forName(leakRefInfoProvider).newInstance();
                }
                builder.withLeakConfig(leakConfig);
            }
            // heap
            element = getFirstElementByTagInRoot(root, "heap");
            if (element != null) {
                final String intervalMillisString = element.getAttribute("intervalMillis");
                HeapConfig heapConfig = new HeapConfig();
                if (!TextUtils.isEmpty(intervalMillisString)) {
                    heapConfig.intervalMillis = Long.parseLong(intervalMillisString);
                }
                builder.withHeapConfig(heapConfig);
            }
            // pss
            element = getFirstElementByTagInRoot(root, "pss");
            if (element != null) {
                final String intervalMillisString = element.getAttribute("intervalMillis");
                PssConfig pssConfig = new PssConfig();
                if (!TextUtils.isEmpty(intervalMillisString)) {
                    pssConfig.intervalMillis = Long.parseLong(intervalMillisString);
                }
                builder.withPssConfig(pssConfig);
            }
            // ram
            element = getFirstElementByTagInRoot(root, "ram");
            if (element != null) {
                final String intervalMillisString = element.getAttribute("intervalMillis");
                RamConfig ramConfig = new RamConfig();
                if (!TextUtils.isEmpty(intervalMillisString)) {
                    ramConfig.intervalMillis = Long.parseLong(intervalMillisString);
                }
                builder.withRamConfig(ramConfig);
            }
            // network
            element = getFirstElementByTagInRoot(root, "network");
            if (element != null) {
                builder.withNetworkConfig(new NetworkConfig());
            }
            // sm
            element = getFirstElementByTagInRoot(root, "sm");
            if (element != null) {
                final String debugNotifyString = element.getAttribute("debugNotification");
                final String longBlockThresholdMillisString = element.getAttribute("longBlockThresholdMillis");
                final String shortBlockThresholdMillisString = element.getAttribute("shortBlockThresholdMillis");
                final String dumpIntervalMillisString = element.getAttribute("dumpIntervalMillis");
                SmConfig smConfig = new SmConfig();
                if (!TextUtils.isEmpty(debugNotifyString)) {
                    smConfig.debugNotification = Boolean.parseBoolean(debugNotifyString);
                }
                if (!TextUtils.isEmpty(longBlockThresholdMillisString)) {
                    smConfig.longBlockThresholdMillis = Long.parseLong(longBlockThresholdMillisString);
                }
                if (!TextUtils.isEmpty(shortBlockThresholdMillisString)) {
                    smConfig.shortBlockThresholdMillis = Long.parseLong(shortBlockThresholdMillisString);
                }
                if (!TextUtils.isEmpty(dumpIntervalMillisString)) {
                    smConfig.dumpIntervalMillis = Long.parseLong(dumpIntervalMillisString);
                }
                builder.withSmConfig(smConfig);
            }
            // startup
            element = getFirstElementByTagInRoot(root, "startup");
            if (element != null) {
                builder.withStartupConfig(new StartupConfig());
            }
            // traffic
            element = getFirstElementByTagInRoot(root, "traffic");
            if (element != null) {
                final String intervalMillisString = element.getAttribute("intervalMillis");
                final String sampleMillisString = element.getAttribute("sampleMillis");
                TrafficConfig trafficConfig = new TrafficConfig();
                if (!TextUtils.isEmpty(intervalMillisString)) {
                    trafficConfig.intervalMillis = Long.parseLong(intervalMillisString);
                }
                if (!TextUtils.isEmpty(sampleMillisString)) {
                    trafficConfig.sampleMillis = Long.parseLong(sampleMillisString);
                }
                builder.withTrafficConfig(trafficConfig);
            }
            // crash
            element = getFirstElementByTagInRoot(root, "crash");
            if (element != null) {
                final String crashProviderString = element.getAttribute("crashProvider");
                CrashConfig crashConfig = new CrashConfig();
                if (!TextUtils.isEmpty(crashProviderString)) {
                    crashConfig.crashProvider = (CrashProvider) Class.forName(crashProviderString).newInstance();
                }
                builder.withCrashConfig(crashConfig);
            }
            // thread
            element = getFirstElementByTagInRoot(root, "thread");
            if (element != null) {
                final String intervalMillisString = element.getAttribute("intervalMillis");
                final String threadFilterString = element.getAttribute("threadFilter");
                ThreadConfig threadConfig = new ThreadConfig();
                if (!TextUtils.isEmpty(intervalMillisString)) {
                    threadConfig.intervalMillis = Long.parseLong(intervalMillisString);
                }
                if (!TextUtils.isEmpty(threadFilterString)) {
                    threadConfig.threadFilter = (ThreadFilter) Class.forName(threadFilterString).newInstance();
                }
                builder.withThreadConfig(threadConfig);
            }
            // pageload
            element = getFirstElementByTagInRoot(root, "pageload");
            if (element != null) {
                final String pageInfoProvider = element.getAttribute("pageInfoProvider");
                PageloadConfig pageloadConfig = new PageloadConfig();
                if (!TextUtils.isEmpty(pageInfoProvider)) {
                    pageloadConfig.pageInfoProvider = (PageInfoProvider) Class.forName(pageInfoProvider).newInstance();
                }
                builder.withPageloadConfig(pageloadConfig);
            }
            // methodCanary
            element = getFirstElementByTagInRoot(root, "methodCanary");
            if (element != null) {
                final String maxMethodCountSingleThreadByCostString = element.getAttribute("maxMethodCountSingleThreadByCost");
                final String lowCostMethodThresholdMillisString = element.getAttribute("lowCostMethodThresholdMillis");
                MethodCanaryConfig methodCanaryConfig = new MethodCanaryConfig();
                if (!TextUtils.isEmpty(maxMethodCountSingleThreadByCostString)) {
                    methodCanaryConfig.maxMethodCountSingleThreadByCost = Integer.parseInt(maxMethodCountSingleThreadByCostString);
                }
                if (!TextUtils.isEmpty(lowCostMethodThresholdMillisString)) {
                    methodCanaryConfig.lowCostMethodThresholdMillis = Long.parseLong(lowCostMethodThresholdMillisString);
                }
                builder.withMethodCanaryConfig(methodCanaryConfig);
            }
        } catch (Exception e) {
            L.e(e);
        }
        return builder.build();
    }

    private static @Nullable
    Element getFirstElementByTagInRoot(Element root, String moduleName) {
        NodeList elements = root.getElementsByTagName(moduleName);
        if (elements != null && elements.getLength() == 1) {
            return (Element) elements.item(0);
        }
        return null;
    }

    public static GodEyeConfig fromAssets(String assetsPath) {
        InputStream is = null;
        try {
            is = GodEye.instance().getApplication().getAssets().open(assetsPath);
            return fromInputStream(is);
        } catch (Exception e) {
            L.e(e);
            return defaultConfig();
        } finally {
            IoUtil.closeSilently(is);
        }
    }

    public static class CpuConfig implements CpuContext, Serializable {
        public long intervalMillis;
        public long sampleMillis;

        public CpuConfig(long intervalMillis, long sampleMillis) {
            this.intervalMillis = intervalMillis;
            this.sampleMillis = sampleMillis;
        }

        public CpuConfig() {
            this.intervalMillis = 2000;
            this.sampleMillis = 2000;
        }

        @Override
        public long intervalMillis() {
            return intervalMillis;
        }

        @Override
        public long sampleMillis() {
            return sampleMillis;
        }

        @Override
        public String toString() {
            return "CpuConfig{" +
                    "intervalMillis=" + intervalMillis +
                    ", sampleMillis=" + sampleMillis +
                    '}';
        }
    }

    public static class BatteryConfig implements BatteryContext, Serializable {

        public BatteryConfig() {
        }

        @Override
        public Context context() {
            return GodEye.instance().getApplication();
        }

        @Override
        public String toString() {
            return "BatteryConfig{}";
        }
    }

    public static class FpsConfig implements FpsContext, Serializable {
        public long intervalMillis;

        public FpsConfig(long intervalMillis) {
            this.intervalMillis = intervalMillis;
        }

        public FpsConfig() {
            this.intervalMillis = 2000;
        }

        @Override
        public Context context() {
            return GodEye.instance().getApplication();
        }

        @Override
        public long intervalMillis() {
            return intervalMillis;
        }

        @Override
        public String toString() {
            return "FpsConfig{" +
                    "intervalMillis=" + intervalMillis +
                    '}';
        }
    }

    public static class LeakConfig implements LeakContext, Serializable {
        // if you want leak module work in production,set debug false
        public boolean debug;
        public boolean debugNotification;
        public LeakRefInfoProvider leakRefInfoProvider;

        public LeakConfig(boolean debug, boolean debugNotification, LeakRefInfoProvider leakRefInfoProvider) {
            this.debug = debug;
            this.debugNotification = debugNotification;
            this.leakRefInfoProvider = leakRefInfoProvider;
        }

        public LeakConfig() {
            this.debug = true;
            this.debugNotification = true;
            this.leakRefInfoProvider = new DefaultLeakRefInfoProvider();
        }

        @NonNull
        @Override
        public Application application() {
            return GodEye.instance().getApplication();
        }

        @Override
        public boolean debug() {
            return debug;
        }

        @Override
        public boolean debugNotification() {
            return debugNotification;
        }

        @NonNull
        @Override
        public LeakRefInfoProvider leakRefInfoProvider() {
            return leakRefInfoProvider == null ? new DefaultLeakRefInfoProvider() : leakRefInfoProvider;
        }

        @Override
        public String toString() {
            return "LeakConfig{" +
                    "debug=" + debug +
                    ", debugNotification=" + debugNotification +
                    ", leakRefInfoProvider=" + leakRefInfoProvider +
                    '}';
        }
    }


    public static class HeapConfig implements HeapContext, Serializable {
        public long intervalMillis;

        public HeapConfig(long intervalMillis) {
            this.intervalMillis = intervalMillis;
        }

        public HeapConfig() {
            this.intervalMillis = 2000;
        }

        @Override
        public long intervalMillis() {
            return intervalMillis;
        }

        @Override
        public String toString() {
            return "HeapConfig{" +
                    "intervalMillis=" + intervalMillis +
                    '}';
        }
    }


    public static class PssConfig implements PssContext, Serializable {
        public long intervalMillis;

        public PssConfig(long intervalMillis) {
            this.intervalMillis = intervalMillis;
        }

        public PssConfig() {
            this.intervalMillis = 2000;
        }

        @Override
        public Context context() {
            return GodEye.instance().getApplication();
        }

        @Override
        public long intervalMillis() {
            return intervalMillis;
        }

        @Override
        public String toString() {
            return "PssConfig{" +
                    "intervalMillis=" + intervalMillis +
                    '}';
        }
    }

    public static class RamConfig implements RamContext, Serializable {
        public long intervalMillis;

        public RamConfig(long intervalMillis) {
            this.intervalMillis = intervalMillis;
        }

        public RamConfig() {
            this.intervalMillis = 3000;
        }

        @Override
        public Context context() {
            return GodEye.instance().getApplication();
        }

        @Override
        public long intervalMillis() {
            return intervalMillis;
        }

        @Override
        public String toString() {
            return "RamConfig{" +
                    "intervalMillis=" + intervalMillis +
                    '}';
        }
    }


    public static class NetworkConfig implements NetworkContext, Serializable {
        @Override
        public String toString() {
            return "NetworkConfig{}";
        }
    }

    public static class SmConfig implements SmContext, Serializable {
        public boolean debugNotification;
        public long longBlockThresholdMillis;
        public long shortBlockThresholdMillis;
        public long dumpIntervalMillis;

        public SmConfig(boolean debugNotification, long longBlockThresholdMillis, long shortBlockThresholdMillis, long dumpIntervalMillis) {
            this.debugNotification = debugNotification;
            this.longBlockThresholdMillis = longBlockThresholdMillis;
            this.shortBlockThresholdMillis = shortBlockThresholdMillis;
            this.dumpIntervalMillis = dumpIntervalMillis;
        }

        public SmConfig() {
            this.debugNotification = true;
            this.longBlockThresholdMillis = 500;
            this.shortBlockThresholdMillis = 500;
            this.dumpIntervalMillis = 1000;
        }

        @Override
        public Context context() {
            return GodEye.instance().getApplication();
        }

        @Override
        public boolean debugNotification() {
            return debugNotification;
        }

        @Override
        public long longBlockThreshold() {
            return longBlockThresholdMillis;
        }

        @Override
        public long shortBlockThreshold() {
            return shortBlockThresholdMillis;
        }

        @Override
        public long dumpInterval() {
            return dumpIntervalMillis;
        }

        @Override
        public String toString() {
            return "SmConfig{" +
                    "debugNotification=" + debugNotification +
                    ", longBlockThresholdMillis=" + longBlockThresholdMillis +
                    ", shortBlockThresholdMillis=" + shortBlockThresholdMillis +
                    ", dumpIntervalMillis=" + dumpIntervalMillis +
                    '}';
        }

        public static class Factory {

            public static SmConfig convert(SmContext smContext) {
                if (smContext == null) {
                    return null;
                }
                return new SmConfig(smContext.debugNotification(), smContext.longBlockThreshold(), smContext.shortBlockThreshold(), smContext.dumpInterval());
            }

            public static SmContext convert(GodEyeConfig.SmConfig smConfig) {
                if (smConfig == null) {
                    return null;
                }
                return new SmContext() {
                    @Override
                    public Context context() {
                        return smConfig.context();
                    }

                    @Override
                    public boolean debugNotification() {
                        return smConfig.debugNotification();
                    }

                    @Override
                    public long longBlockThreshold() {
                        return smConfig.longBlockThreshold();
                    }

                    @Override
                    public long shortBlockThreshold() {
                        return smConfig.shortBlockThreshold();
                    }

                    @Override
                    public long dumpInterval() {
                        return smConfig.dumpInterval();
                    }
                };
            }
        }
    }

    public static class StartupConfig implements StartupContext, Serializable {
        @Override
        public String toString() {
            return "StartupConfig{}";
        }
    }

    public static class TrafficConfig implements TrafficContext, Serializable {
        public long intervalMillis;
        public long sampleMillis;

        public TrafficConfig(long intervalMillis, long sampleMillis) {
            this.intervalMillis = intervalMillis;
            this.sampleMillis = sampleMillis;
        }

        public TrafficConfig() {
            this.intervalMillis = 2000;
            this.sampleMillis = 1000;
        }

        @Override
        public long intervalMillis() {
            return intervalMillis;
        }

        @Override
        public long sampleMillis() {
            return sampleMillis;
        }

        @Override
        public String toString() {
            return "TrafficConfig{" +
                    "intervalMillis=" + intervalMillis +
                    ", sampleMillis=" + sampleMillis +
                    '}';
        }
    }

    public static class CrashConfig implements CrashProvider, Serializable {
        public CrashProvider crashProvider;

        public CrashConfig(CrashProvider crashProvider) {
            this.crashProvider = crashProvider;
        }

        public CrashConfig() {
            this.crashProvider = new CrashFileProvider();
        }

        @Override
        public void storeCrash(CrashInfo crashInfo) throws Throwable {
            crashProvider.storeCrash(crashInfo);
        }

        @Override
        public List<CrashInfo> restoreCrash() throws Throwable {
            return crashProvider.restoreCrash();
        }

        @Override
        public String toString() {
            return "CrashConfig{" +
                    "crashProvider=" + crashProvider +
                    '}';
        }
    }

    public static class ThreadConfig implements ThreadContext, Serializable {
        public long intervalMillis;
        public ThreadFilter threadFilter;

        public ThreadConfig(long intervalMillis, ThreadFilter threadFilter) {
            this.intervalMillis = intervalMillis;
            this.threadFilter = threadFilter;
        }

        public ThreadConfig() {
            this.intervalMillis = 2000;
            this.threadFilter = new ExcludeSystemThreadFilter();
        }

        @Override
        public long intervalMillis() {
            return intervalMillis;
        }

        @Override
        public ThreadFilter threadFilter() {
            return threadFilter;
        }

        @Override
        public String toString() {
            return "ThreadConfig{" +
                    "intervalMillis=" + intervalMillis +
                    ", threadFilter=" + threadFilter +
                    '}';
        }
    }


    public static class PageloadConfig implements PageloadContext, Serializable {
        public PageInfoProvider pageInfoProvider;

        public PageloadConfig() {
            this.pageInfoProvider = new DefaultPageInfoProvider();
        }

        public PageloadConfig(PageInfoProvider pageInfoProvider) {
            this.pageInfoProvider = pageInfoProvider;
        }

        @Override
        public Application application() {
            return GodEye.instance().getApplication();
        }

        @NonNull
        @Override
        public PageInfoProvider pageInfoProvider() {
            return pageInfoProvider;
        }

        @Override
        public String toString() {
            return "PageloadConfig{" +
                    "pageInfoProvider=" + pageInfoProvider +
                    '}';
        }
    }

    public static class MethodCanaryConfig implements MethodCanaryContext, Serializable {
        public int maxMethodCountSingleThreadByCost;
        public long lowCostMethodThresholdMillis;

        public MethodCanaryConfig() {
            this.maxMethodCountSingleThreadByCost = 300;
            this.lowCostMethodThresholdMillis = 10L;
        }

        public MethodCanaryConfig(int maxMethodCountSingleThreadByCost, long lowCostMethodThresholdMillis) {
            this.maxMethodCountSingleThreadByCost = maxMethodCountSingleThreadByCost;
            this.lowCostMethodThresholdMillis = lowCostMethodThresholdMillis;
        }

        @Override
        public long lowCostMethodThresholdMillis() {
            return lowCostMethodThresholdMillis;
        }

        @Override
        public int maxMethodCountSingleThreadByCost() {
            return maxMethodCountSingleThreadByCost;
        }

        @Override
        public Application app() {
            return GodEye.instance().getApplication();
        }

        @Override
        public String toString() {
            return "MethodCanaryConfig{" +
                    "maxMethodCountSingleThreadByCost=" + maxMethodCountSingleThreadByCost +
                    ", lowCostMethodThresholdMillis=" + lowCostMethodThresholdMillis +
                    '}';
        }
    }

    private CpuConfig mCpuConfig;
    private BatteryConfig mBatteryConfig;
    private FpsConfig mFpsConfig;
    private LeakConfig mLeakConfig;
    private HeapConfig mHeapConfig;
    private PssConfig mPssConfig;
    private RamConfig mRamConfig;
    private NetworkConfig mNetworkConfig;
    private SmConfig mSmConfig;
    private StartupConfig mStartupConfig;
    private TrafficConfig mTrafficConfig;
    private CrashConfig mCrashConfig;
    private ThreadConfig mThreadConfig;
    private PageloadConfig mPageloadConfig;
    private MethodCanaryConfig mMethodCanaryConfig;

    private GodEyeConfig() {
    }

    public CpuConfig getCpuConfig() {
        return mCpuConfig;
    }

    public void setCpuConfig(CpuConfig cpuConfig) {
        mCpuConfig = cpuConfig;
    }

    public BatteryConfig getBatteryConfig() {
        return mBatteryConfig;
    }

    public void setBatteryConfig(BatteryConfig batteryConfig) {
        mBatteryConfig = batteryConfig;
    }

    public FpsConfig getFpsConfig() {
        return mFpsConfig;
    }

    public void setFpsConfig(FpsConfig fpsConfig) {
        mFpsConfig = fpsConfig;
    }

    public LeakConfig getLeakConfig() {
        return mLeakConfig;
    }

    public void setLeakConfig(LeakConfig leakConfig) {
        mLeakConfig = leakConfig;
    }

    public HeapConfig getHeapConfig() {
        return mHeapConfig;
    }

    public void setHeapConfig(HeapConfig heapConfig) {
        mHeapConfig = heapConfig;
    }

    public PssConfig getPssConfig() {
        return mPssConfig;
    }

    public void setPssConfig(PssConfig pssConfig) {
        mPssConfig = pssConfig;
    }

    public RamConfig getRamConfig() {
        return mRamConfig;
    }

    public void setRamConfig(RamConfig ramConfig) {
        mRamConfig = ramConfig;
    }

    public NetworkConfig getNetworkConfig() {
        return mNetworkConfig;
    }

    public void setNetworkConfig(NetworkConfig networkConfig) {
        mNetworkConfig = networkConfig;
    }

    public SmConfig getSmConfig() {
        return mSmConfig;
    }

    public void setSmConfig(SmConfig smConfig) {
        mSmConfig = smConfig;
    }

    public StartupConfig getStartupConfig() {
        return mStartupConfig;
    }

    public void setStartupConfig(StartupConfig startupConfig) {
        mStartupConfig = startupConfig;
    }

    public TrafficConfig getTrafficConfig() {
        return mTrafficConfig;
    }

    public void setTrafficConfig(TrafficConfig trafficConfig) {
        mTrafficConfig = trafficConfig;
    }

    public CrashConfig getCrashConfig() {
        return mCrashConfig;
    }

    public void setCrashConfig(CrashConfig crashConfig) {
        mCrashConfig = crashConfig;
    }

    public ThreadConfig getThreadConfig() {
        return mThreadConfig;
    }

    public void setThreadConfig(ThreadConfig threadConfig) {
        mThreadConfig = threadConfig;
    }

    public PageloadConfig getPageloadConfig() {
        return mPageloadConfig;
    }

    public void setPageloadConfig(PageloadConfig pageloadConfig) {
        mPageloadConfig = pageloadConfig;
    }

    public MethodCanaryConfig getMethodCanaryConfig() {
        return mMethodCanaryConfig;
    }

    public void setMethodCanaryConfig(MethodCanaryConfig methodCanaryConfig) {
        mMethodCanaryConfig = methodCanaryConfig;
    }

    @Override
    public String toString() {
        return "GodEyeConfig{" +
                "mCpuConfig=" + mCpuConfig +
                ", mBatteryConfig=" + mBatteryConfig +
                ", mFpsConfig=" + mFpsConfig +
                ", mLeakConfig=" + mLeakConfig +
                ", mHeapConfig=" + mHeapConfig +
                ", mPssConfig=" + mPssConfig +
                ", mRamConfig=" + mRamConfig +
                ", mNetworkConfig=" + mNetworkConfig +
                ", mSmConfig=" + mSmConfig +
                ", mStartupConfig=" + mStartupConfig +
                ", mTrafficConfig=" + mTrafficConfig +
                ", mCrashConfig=" + mCrashConfig +
                ", mThreadConfig=" + mThreadConfig +
                ", mPageloadConfig=" + mPageloadConfig +
                ", mMethodCanaryConfig=" + mMethodCanaryConfig +
                '}';
    }

    public static final class GodEyeConfigBuilder {
        private CpuConfig mCpuConfig;
        private BatteryConfig mBatteryConfig;
        private FpsConfig mFpsConfig;
        private LeakConfig mLeakConfig;
        private HeapConfig mHeapConfig;
        private PssConfig mPssConfig;
        private RamConfig mRamConfig;
        private NetworkConfig mNetworkConfig;
        private SmConfig mSmConfig;
        private StartupConfig mStartupConfig;
        private TrafficConfig mTrafficConfig;
        private CrashConfig mCrashConfig;
        private ThreadConfig mThreadConfig;
        private PageloadConfig mPageloadConfig;
        private MethodCanaryConfig mMethodCanaryConfig;

        public static GodEyeConfigBuilder godEyeConfig() {
            return new GodEyeConfigBuilder();
        }

        public GodEyeConfigBuilder withCpuConfig(CpuConfig CpuConfig) {
            this.mCpuConfig = CpuConfig;
            return this;
        }

        public GodEyeConfigBuilder withBatteryConfig(BatteryConfig BatteryConfig) {
            this.mBatteryConfig = BatteryConfig;
            return this;
        }

        public GodEyeConfigBuilder withFpsConfig(FpsConfig FpsConfig) {
            this.mFpsConfig = FpsConfig;
            return this;
        }

        public GodEyeConfigBuilder withLeakConfig(LeakConfig LeakConfig) {
            this.mLeakConfig = LeakConfig;
            return this;
        }

        public GodEyeConfigBuilder withHeapConfig(HeapConfig HeapConfig) {
            this.mHeapConfig = HeapConfig;
            return this;
        }

        public GodEyeConfigBuilder withPssConfig(PssConfig PssConfig) {
            this.mPssConfig = PssConfig;
            return this;
        }

        public GodEyeConfigBuilder withRamConfig(RamConfig RamConfig) {
            this.mRamConfig = RamConfig;
            return this;
        }

        public GodEyeConfigBuilder withNetworkConfig(NetworkConfig NetworkConfig) {
            this.mNetworkConfig = NetworkConfig;
            return this;
        }

        public GodEyeConfigBuilder withSmConfig(SmConfig SmConfig) {
            this.mSmConfig = SmConfig;
            return this;
        }

        public GodEyeConfigBuilder withStartupConfig(StartupConfig StartupConfig) {
            this.mStartupConfig = StartupConfig;
            return this;
        }

        public GodEyeConfigBuilder withTrafficConfig(TrafficConfig TrafficConfig) {
            this.mTrafficConfig = TrafficConfig;
            return this;
        }

        public GodEyeConfigBuilder withCrashConfig(CrashConfig CrashConfig) {
            this.mCrashConfig = CrashConfig;
            return this;
        }

        public GodEyeConfigBuilder withThreadConfig(ThreadConfig ThreadConfig) {
            this.mThreadConfig = ThreadConfig;
            return this;
        }

        public GodEyeConfigBuilder withPageloadConfig(PageloadConfig PageloadConfig) {
            this.mPageloadConfig = PageloadConfig;
            return this;
        }

        public GodEyeConfigBuilder withMethodCanaryConfig(MethodCanaryConfig MethodCanaryConfig) {
            this.mMethodCanaryConfig = MethodCanaryConfig;
            return this;
        }

        public GodEyeConfig build() {
            GodEyeConfig godEyeConfig = new GodEyeConfig();
            godEyeConfig.mStartupConfig = this.mStartupConfig;
            godEyeConfig.mMethodCanaryConfig = this.mMethodCanaryConfig;
            godEyeConfig.mHeapConfig = this.mHeapConfig;
            godEyeConfig.mFpsConfig = this.mFpsConfig;
            godEyeConfig.mNetworkConfig = this.mNetworkConfig;
            godEyeConfig.mLeakConfig = this.mLeakConfig;
            godEyeConfig.mTrafficConfig = this.mTrafficConfig;
            godEyeConfig.mPageloadConfig = this.mPageloadConfig;
            godEyeConfig.mPssConfig = this.mPssConfig;
            godEyeConfig.mSmConfig = this.mSmConfig;
            godEyeConfig.mRamConfig = this.mRamConfig;
            godEyeConfig.mBatteryConfig = this.mBatteryConfig;
            godEyeConfig.mThreadConfig = this.mThreadConfig;
            godEyeConfig.mCrashConfig = this.mCrashConfig;
            godEyeConfig.mCpuConfig = this.mCpuConfig;
            return godEyeConfig;
        }
    }
}
