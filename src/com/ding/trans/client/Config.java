package com.ding.trans.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;

public class Config {

    public static final String CONFIG_FILE = "lunatrans.properties";

    public static final String MAGIC_PHRASE = "MagicPhrase";

    public static final String DEFAULT_REMOTE_URL = "http://localhost:18020";

    private static Properties props = new Properties();

    static {
        File overrideFile = getOverrideConfigFile();
        try {
            readCustomConfig(new FileInputStream(overrideFile));
        } catch (Exception e) {
            readCustomConfig(getResourceAsStream(CONFIG_FILE));
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                writeCustomConfig(overrideFile);
            }
        });
    }

    public static URL getResource(String resName) {
        return Config.class.getClassLoader().getResource(resName);
    }

    public static InputStream getResourceAsStream(String resName) {
        return Config.class.getClassLoader().getResourceAsStream(resName);
    }

    public static String getApplicationDataDir() {
        String userHome = System.getProperty("user.home");
        File appDataDir = new File(userHome + File.separator + ".lunatrans");
        return ClientUtil.normPath(appDataDir.getAbsolutePath());
    }

    public static String getRemoteUrl() {
        return props.getProperty("RemoteUrl", DEFAULT_REMOTE_URL);
    }

    public static String getValue(String key) {
        if (key.equals(MAGIC_PHRASE)) {
            return calculateMagicPhrase();
        }
        String value = props.getProperty(key);
        if (value == null) {
            // fall back
            readCustomConfig(getResourceAsStream(CONFIG_FILE));
            return props.getProperty(key);
        } else {
            return value;
        }
    }

    public static void setValue(String key, String value) {
        props.setProperty(key, value);
    }

    public static int getInteger(String key) {
        String value = getValue(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    public static long getLong(String key) {
        String value = getValue(key);
        return value == null ? 0L : Long.parseLong(value);
    }

    public static double getDouble(String key) {
        String value = getValue(key);
        return value == null ? 0.0 : Double.parseDouble(value);
    }

    public static boolean getBoolean(String key) {
        String value = getValue(key);
        return value == null ? false : Boolean.parseBoolean(value);
    }

    private static void readCustomConfig(InputStream stream) {
        try {
            Properties custom = new Properties();
            custom.load(stream);
            for (Entry<Object, Object> entry : custom.entrySet()) {
                if (!props.containsKey(entry.getKey())) {
                    props.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private static void writeCustomConfig(File overrideFile) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(overrideFile);
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ingore
                }
            }
        }
    }

    private static File getOverrideConfigFile() {
        return new File(getApplicationDataDir() + File.separator + CONFIG_FILE);
    }

    private static String calculateMagicPhrase() {
        StringBuilder sb = new StringBuilder();
        sb.append((char) 0x6C).append((char) 0x75).append((char) 0x6E).append((char) 0x61);
        sb.append((char) 0x32).append((char) 0x30).append((char) 0x31).append((char) 0x35);
        return sb.toString();
    }

}
