package robolectricutils;

import org.junit.runners.model.InitializationError;
import org.neidhardt.dynamicsoundboard.BuildConfig;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * File created by eric.neidhardt on 02.04.2015.
 */
@SuppressWarnings("WeakerAccess") // required for test runner
public class CustomTestRunner extends RobolectricTestRunner {

	public CustomTestRunner(Class<?> classInstance) throws InitializationError {
		super(classInstance);
	}

	@Override
	protected AndroidManifest getAppManifest(Config config) {
		String path = "src/main/AndroidManifest.xml";

		// android studio has a different execution root for tests than pure gradle
		// so we avoid here manual effort to get them running inside android studio
		if (!new File(path).exists())
			path = "app/" + path;

		config = overwriteConfig(config, "manifest", path);
		return super.getAppManifest(config);
	}

	private Config.Implementation overwriteConfig(Config config, String key, String value)
	{
		Properties properties = new Properties();
		properties.setProperty(key, value);
		return new Config.Implementation(config, Config.Implementation.fromProperties(properties));
	}

	@Override
	public Config getConfig(Method method) {
		Config config = super.getConfig(method);
		// Fixing up the Config:
		// SDK can not be higher than 21
		// constants must point to a real BuildConfig class
		//
		int[] sdks = {16};
		config = new Config.Implementation
				(
						sdks,
						config.manifest(),
						config.qualifiers(),
						config.packageName(),
						config.abiSplit(),
						config.resourceDir(),
						config.assetDir(),
						config.buildDir(),
						config.shadows(),
						config.instrumentedPackages(),
						config.application(),
						config.libraries(),
						ensureBuildConfig(config.constants())
				);

		return config;
	}

	private Class<?> ensureBuildConfig(Class<?> constants) {
		if (constants == Void.class) return BuildConfig.class;
		return constants;
	}

}

