package mx.itesm.lightsgone;

import android.content.pm.PackageInfo;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;


public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String version = "";
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		}
		catch (Exception e){

		}
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Juego(version), config);
	}
}
