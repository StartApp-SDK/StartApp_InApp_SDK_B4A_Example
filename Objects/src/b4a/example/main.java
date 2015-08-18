package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public com.startapp.android.publish.b4a.StartAppSDKWrapper _sdk = null;
public com.startapp.android.publish.b4a.StartAppAdWrapper _startappsplash = null;
public com.startapp.android.publish.b4a.StartAppAdWrapper _startappinterstitial = null;
public com.startapp.android.publish.b4a.StartAppAdWrapper _startapprewardedvideo = null;
public static String _appid = "";
public static boolean _enablereturnads = false;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 29;BA.debugLine="Activity.LoadLayout(\"activity_main\")";
mostCurrent._activity.LoadLayout("activity_main",mostCurrent.activityBA);
 //BA.debugLineNum = 31;BA.debugLine="initializeObjects";
_initializeobjects();
 //BA.debugLineNum = 32;BA.debugLine="initializeSDK";
_initializesdk();
 //BA.debugLineNum = 34;BA.debugLine="showSplashAd(FirstTime)";
_showsplashad(_firsttime);
 //BA.debugLineNum = 38;BA.debugLine="loadInterstitialAd";
_loadinterstitialad();
 //BA.debugLineNum = 40;BA.debugLine="showBanner";
_showbanner();
 //BA.debugLineNum = 44;BA.debugLine="loadRewardedVideo";
_loadrewardedvideo();
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 95;BA.debugLine="Sub Activity_KeyPress(KeyCode As Int) As Boolean";
 //BA.debugLineNum = 96;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 97;BA.debugLine="startAppInterstitial.onBackPressed";
mostCurrent._startappinterstitial.onBackPressed();
 };
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 89;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 90;BA.debugLine="startAppInterstitial.onPause";
mostCurrent._startappinterstitial.onPause();
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 85;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 86;BA.debugLine="startAppInterstitial.onResume";
mostCurrent._startappinterstitial.onResume();
 //BA.debugLineNum = 87;BA.debugLine="End Sub";
return "";
}
public static String  _addisplaylistener_adclicked(com.startapp.android.publish.b4a.AdWrapper _ad1) throws Exception{
 //BA.debugLineNum = 121;BA.debugLine="Sub AdDisplayListener_AdClicked(ad1 As Ad)";
 //BA.debugLineNum = 122;BA.debugLine="ToastMessageShow (\"Ad Clicked\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ad Clicked",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 123;BA.debugLine="End Sub";
return "";
}
public static String  _addisplaylistener_adhidden(com.startapp.android.publish.b4a.AdWrapper _ad1) throws Exception{
 //BA.debugLineNum = 117;BA.debugLine="Sub AdDisplayListener_AdHidden(ad1 As Ad)";
 //BA.debugLineNum = 118;BA.debugLine="ToastMessageShow (\"Ad Hidden\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ad Hidden",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 119;BA.debugLine="End Sub";
return "";
}
public static String  _addisplaylistener_adnotdisplayed(com.startapp.android.publish.b4a.AdWrapper _ad1) throws Exception{
 //BA.debugLineNum = 125;BA.debugLine="Sub AdDisplayListener_AdNotDisplayed(ad1 As Ad)";
 //BA.debugLineNum = 126;BA.debugLine="ToastMessageShow (\"Ad Not Displayed: \" & ad1.NotD";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ad Not Displayed: "+_ad1.getNotDisplayedReason(),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static String  _btnshowinterstitialclicked_click() throws Exception{
 //BA.debugLineNum = 103;BA.debugLine="Sub btnShowInterstitialClicked_Click";
 //BA.debugLineNum = 104;BA.debugLine="startAppInterstitial.showAd";
mostCurrent._startappinterstitial.showAd(mostCurrent.activityBA);
 //BA.debugLineNum = 105;BA.debugLine="End Sub";
return "";
}
public static String  _btnshowrewardedvideoclicked_click() throws Exception{
 //BA.debugLineNum = 111;BA.debugLine="Sub btnShowRewardedVideoClicked_Click";
 //BA.debugLineNum = 112;BA.debugLine="startAppRewardedVideo.showAd";
mostCurrent._startapprewardedvideo.showAd(mostCurrent.activityBA);
 //BA.debugLineNum = 113;BA.debugLine="End Sub";
return "";
}
public static String  _btwshowinterstitialwithcallbackclicked_click() throws Exception{
 //BA.debugLineNum = 107;BA.debugLine="Sub btwShowInterstitialWithCallbackClicked_Click";
 //BA.debugLineNum = 108;BA.debugLine="startAppInterstitial.showAdWithListener(\"AdDispla";
mostCurrent._startappinterstitial.showAdWithListener(mostCurrent.activityBA,"AdDisplayListener");
 //BA.debugLineNum = 109;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 18;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 19;BA.debugLine="Dim sdk As StartAppSDK";
mostCurrent._sdk = new com.startapp.android.publish.b4a.StartAppSDKWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim startAppSplash As StartAppAd";
mostCurrent._startappsplash = new com.startapp.android.publish.b4a.StartAppAdWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim startAppInterstitial As StartAppAd";
mostCurrent._startappinterstitial = new com.startapp.android.publish.b4a.StartAppAdWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim startAppRewardedVideo As StartAppAd";
mostCurrent._startapprewardedvideo = new com.startapp.android.publish.b4a.StartAppAdWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim appID As String = \"B4AExampleApp\"";
mostCurrent._appid = "B4AExampleApp";
 //BA.debugLineNum = 24;BA.debugLine="Dim enableReturnAds As Boolean = True";
_enablereturnads = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _initializeobjects() throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub initializeObjects";
 //BA.debugLineNum = 49;BA.debugLine="startAppSplash.initialize";
mostCurrent._startappsplash.initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 50;BA.debugLine="startAppInterstitial.initialize";
mostCurrent._startappinterstitial.initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 51;BA.debugLine="startAppRewardedVideo.initialize";
mostCurrent._startapprewardedvideo.initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return "";
}
public static String  _initializesdk() throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub initializeSDK";
 //BA.debugLineNum = 55;BA.debugLine="sdk.init(appID, enableReturnAds)";
mostCurrent._sdk.init(mostCurrent.activityBA,mostCurrent._appid,_enablereturnads);
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _loadinterstitialad() throws Exception{
 //BA.debugLineNum = 68;BA.debugLine="Sub loadInterstitialAd";
 //BA.debugLineNum = 69;BA.debugLine="startAppInterstitial.loadAd";
mostCurrent._startappinterstitial.loadAd(mostCurrent.activityBA);
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _loadrewardedvideo() throws Exception{
com.startapp.android.publish.b4a.StartAppAdWrapper.AdModeTypeWrapper _ad_mode = null;
 //BA.debugLineNum = 78;BA.debugLine="Sub loadRewardedVideo";
 //BA.debugLineNum = 79;BA.debugLine="Dim ad_mode As AdMode";
_ad_mode = new com.startapp.android.publish.b4a.StartAppAdWrapper.AdModeTypeWrapper();
 //BA.debugLineNum = 80;BA.debugLine="startAppRewardedVideo.setVideoListener(\"VideoList";
mostCurrent._startapprewardedvideo.setVideoListener(mostCurrent.activityBA,"VideoListener");
 //BA.debugLineNum = 81;BA.debugLine="startAppRewardedVideo.loadAdWithAdMode(ad_mode.RE";
mostCurrent._startapprewardedvideo.loadAdWithAdMode(mostCurrent.activityBA,_ad_mode.REWARDED_VIDEO);
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="End Sub";
return "";
}
public static String  _showbanner() throws Exception{
com.startapp.android.publish.b4a.banner.BannerWrapper _autobanner = null;
com.startapp.android.publish.b4a.banner.BannerWrapper.BannerPositionWrapper _bannerpositiontoset = null;
 //BA.debugLineNum = 72;BA.debugLine="Sub showBanner";
 //BA.debugLineNum = 73;BA.debugLine="Dim autoBanner As Banner";
_autobanner = new com.startapp.android.publish.b4a.banner.BannerWrapper();
 //BA.debugLineNum = 74;BA.debugLine="Dim bannerPositionToSet As BannerPosition";
_bannerpositiontoset = new com.startapp.android.publish.b4a.banner.BannerWrapper.BannerPositionWrapper();
 //BA.debugLineNum = 75;BA.debugLine="autoBanner.addBannerWithPosition(bannerPositionTo";
_autobanner.addBannerWithPosition(mostCurrent.activityBA,_bannerpositiontoset.BOTTOM);
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _showsplashad(boolean _firsttime) throws Exception{
com.startapp.android.publish.b4a.splash.SplashConfigWrapper _splashconf = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _logotoset = null;
 //BA.debugLineNum = 58;BA.debugLine="Sub showSplashAd(FirstTime As Boolean)";
 //BA.debugLineNum = 59;BA.debugLine="Dim splashConf As SplashConfig";
_splashconf = new com.startapp.android.publish.b4a.splash.SplashConfigWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Dim logoToSet As Bitmap";
_logotoset = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 61;BA.debugLine="splashConf.initialize";
_splashconf.initialize(processBA);
 //BA.debugLineNum = 62;BA.debugLine="splashConf.setAppName(\"B4A with StartApp 3.1.2\")";
_splashconf.setAppName(processBA,"B4A with StartApp 3.1.2");
 //BA.debugLineNum = 63;BA.debugLine="logoToSet.initialize(File.DirAssets, \"logo.png\")";
_logotoset.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"logo.png");
 //BA.debugLineNum = 64;BA.debugLine="splashConf.setLogo(logoToSet)";
_splashconf.setLogo(processBA,(android.graphics.Bitmap)(_logotoset.getObject()));
 //BA.debugLineNum = 65;BA.debugLine="startAppSplash.showSplashWithSplashConfig(FirstTi";
mostCurrent._startappsplash.showSplashWithSplashConfig(mostCurrent.activityBA,_firsttime,_splashconf);
 //BA.debugLineNum = 66;BA.debugLine="End Sub";
return "";
}
public static String  _videolistener_videocompleted() throws Exception{
 //BA.debugLineNum = 131;BA.debugLine="Sub VideoListener_VideoCompleted";
 //BA.debugLineNum = 132;BA.debugLine="ToastMessageShow (\"Video Completed\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Video Completed",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 133;BA.debugLine="End Sub";
return "";
}
}
