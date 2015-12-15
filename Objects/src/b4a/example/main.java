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
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
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
  public Object[] GetGlobals() {
		return new Object[] {"Activity",mostCurrent._activity,"appID",mostCurrent._appid,"enableReturnAds",_enablereturnads,"sdk",mostCurrent._sdk,"startAppInterstitial",mostCurrent._startappinterstitial,"startAppRewardedVideo",mostCurrent._startapprewardedvideo,"startAppSplash",mostCurrent._startappsplash};
}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}

public static void killProgram() {
     {
            Activity __a = null;
            if (main.previousOne != null) {
				__a = main.previousOne.get();
			}
            else {
                BA ba = main.mostCurrent.processBA.sharedProcessBA.activityBA.get();
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

}
public static String  _activity_create(boolean _firsttime) throws Exception{
try {
		Debug.PushSubsStack("Activity_Create (main) ","main",0,mostCurrent.activityBA,mostCurrent,27);
Debug.locals.put("FirstTime", _firsttime);
 BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(67108864);
 BA.debugLineNum = 29;BA.debugLine="Activity.LoadLayout(\"activity_main\")";
Debug.ShouldStop(268435456);
mostCurrent._activity.LoadLayout("activity_main",mostCurrent.activityBA);
 BA.debugLineNum = 31;BA.debugLine="initializeObjects";
Debug.ShouldStop(1073741824);
_initializeobjects();
 BA.debugLineNum = 32;BA.debugLine="initializeSDK";
Debug.ShouldStop(-2147483648);
_initializesdk();
 BA.debugLineNum = 34;BA.debugLine="showSplashAd(FirstTime)";
Debug.ShouldStop(2);
_showsplashad(_firsttime);
 BA.debugLineNum = 38;BA.debugLine="loadInterstitialAd";
Debug.ShouldStop(32);
_loadinterstitialad();
 BA.debugLineNum = 40;BA.debugLine="showBanner";
Debug.ShouldStop(128);
_showbanner();
 BA.debugLineNum = 44;BA.debugLine="loadRewardedVideo";
Debug.ShouldStop(2048);
_loadrewardedvideo();
 BA.debugLineNum = 46;BA.debugLine="End Sub";
Debug.ShouldStop(8192);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static boolean  _activity_keypress(int _keycode) throws Exception{
try {
		Debug.PushSubsStack("Activity_KeyPress (main) ","main",0,mostCurrent.activityBA,mostCurrent,95);
Debug.locals.put("KeyCode", _keycode);
 BA.debugLineNum = 95;BA.debugLine="Sub Activity_KeyPress(KeyCode As Int) As Boolean";
Debug.ShouldStop(1073741824);
 BA.debugLineNum = 96;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
Debug.ShouldStop(-2147483648);
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 BA.debugLineNum = 97;BA.debugLine="startAppInterstitial.onBackPressed";
Debug.ShouldStop(1);
mostCurrent._startappinterstitial.onBackPressed();
 };
 BA.debugLineNum = 99;BA.debugLine="End Sub";
Debug.ShouldStop(4);
return false;
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_pause(boolean _userclosed) throws Exception{
try {
		Debug.PushSubsStack("Activity_Pause (main) ","main",0,mostCurrent.activityBA,mostCurrent,89);
Debug.locals.put("UserClosed", _userclosed);
 BA.debugLineNum = 89;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(16777216);
 BA.debugLineNum = 90;BA.debugLine="startAppInterstitial.onPause";
Debug.ShouldStop(33554432);
mostCurrent._startappinterstitial.onPause();
 BA.debugLineNum = 91;BA.debugLine="End Sub";
Debug.ShouldStop(67108864);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_resume() throws Exception{
try {
		Debug.PushSubsStack("Activity_Resume (main) ","main",0,mostCurrent.activityBA,mostCurrent,85);
 BA.debugLineNum = 85;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(1048576);
 BA.debugLineNum = 86;BA.debugLine="startAppInterstitial.onResume";
Debug.ShouldStop(2097152);
mostCurrent._startappinterstitial.onResume();
 BA.debugLineNum = 87;BA.debugLine="End Sub";
Debug.ShouldStop(4194304);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _addisplaylistener_adclicked(com.startapp.android.publish.b4a.AdWrapper _ad1) throws Exception{
try {
		Debug.PushSubsStack("AdDisplayListener_AdClicked (main) ","main",0,mostCurrent.activityBA,mostCurrent,125);
Debug.locals.put("ad1", _ad1);
 BA.debugLineNum = 125;BA.debugLine="Sub AdDisplayListener_AdClicked(ad1 As Ad)";
Debug.ShouldStop(268435456);
 BA.debugLineNum = 126;BA.debugLine="ToastMessageShow (\"Ad Clicked\", False)";
Debug.ShouldStop(536870912);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ad Clicked",anywheresoftware.b4a.keywords.Common.False);
 BA.debugLineNum = 127;BA.debugLine="End Sub";
Debug.ShouldStop(1073741824);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _addisplaylistener_addisplayed(com.startapp.android.publish.b4a.AdWrapper _ad1) throws Exception{
try {
		Debug.PushSubsStack("AdDisplayListener_AdDisplayed (main) ","main",0,mostCurrent.activityBA,mostCurrent,117);
Debug.locals.put("ad1", _ad1);
 BA.debugLineNum = 117;BA.debugLine="Sub AdDisplayListener_AdDisplayed(ad1 As Ad)";
Debug.ShouldStop(1048576);
 BA.debugLineNum = 118;BA.debugLine="ToastMessageShow (\"Ad Displayed\", False)";
Debug.ShouldStop(2097152);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ad Displayed",anywheresoftware.b4a.keywords.Common.False);
 BA.debugLineNum = 119;BA.debugLine="End Sub";
Debug.ShouldStop(4194304);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _addisplaylistener_adhidden(com.startapp.android.publish.b4a.AdWrapper _ad1) throws Exception{
try {
		Debug.PushSubsStack("AdDisplayListener_AdHidden (main) ","main",0,mostCurrent.activityBA,mostCurrent,121);
Debug.locals.put("ad1", _ad1);
 BA.debugLineNum = 121;BA.debugLine="Sub AdDisplayListener_AdHidden(ad1 As Ad)";
Debug.ShouldStop(16777216);
 BA.debugLineNum = 122;BA.debugLine="ToastMessageShow (\"Ad Hidden\", False)";
Debug.ShouldStop(33554432);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ad Hidden",anywheresoftware.b4a.keywords.Common.False);
 BA.debugLineNum = 123;BA.debugLine="End Sub";
Debug.ShouldStop(67108864);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _addisplaylistener_adnotdisplayed(com.startapp.android.publish.b4a.AdWrapper _ad1) throws Exception{
try {
		Debug.PushSubsStack("AdDisplayListener_AdNotDisplayed (main) ","main",0,mostCurrent.activityBA,mostCurrent,129);
Debug.locals.put("ad1", _ad1);
 BA.debugLineNum = 129;BA.debugLine="Sub AdDisplayListener_AdNotDisplayed(ad1 As Ad)";
Debug.ShouldStop(1);
 BA.debugLineNum = 130;BA.debugLine="ToastMessageShow (\"Ad Not Displayed: \" & ad1.NotD";
Debug.ShouldStop(2);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ad Not Displayed: "+_ad1.getNotDisplayedReason(),anywheresoftware.b4a.keywords.Common.False);
 BA.debugLineNum = 131;BA.debugLine="End Sub";
Debug.ShouldStop(4);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _btnshowinterstitialclicked_click() throws Exception{
try {
		Debug.PushSubsStack("btnShowInterstitialClicked_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent,103);
 BA.debugLineNum = 103;BA.debugLine="Sub btnShowInterstitialClicked_Click";
Debug.ShouldStop(64);
 BA.debugLineNum = 104;BA.debugLine="startAppInterstitial.showAd";
Debug.ShouldStop(128);
mostCurrent._startappinterstitial.showAd(mostCurrent.activityBA);
 BA.debugLineNum = 105;BA.debugLine="End Sub";
Debug.ShouldStop(256);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _btnshowrewardedvideoclicked_click() throws Exception{
try {
		Debug.PushSubsStack("btnShowRewardedVideoClicked_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent,111);
 BA.debugLineNum = 111;BA.debugLine="Sub btnShowRewardedVideoClicked_Click";
Debug.ShouldStop(16384);
 BA.debugLineNum = 112;BA.debugLine="startAppRewardedVideo.showAd";
Debug.ShouldStop(32768);
mostCurrent._startapprewardedvideo.showAd(mostCurrent.activityBA);
 BA.debugLineNum = 113;BA.debugLine="End Sub";
Debug.ShouldStop(65536);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _btwshowinterstitialwithcallbackclicked_click() throws Exception{
try {
		Debug.PushSubsStack("btwShowInterstitialWithCallbackClicked_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent,107);
 BA.debugLineNum = 107;BA.debugLine="Sub btwShowInterstitialWithCallbackClicked_Click";
Debug.ShouldStop(1024);
 BA.debugLineNum = 108;BA.debugLine="startAppInterstitial.showAdWithListener(\"AdDispl";
Debug.ShouldStop(2048);
mostCurrent._startappinterstitial.showAdWithListener(mostCurrent.activityBA,"AdDisplayListener");
 BA.debugLineNum = 109;BA.debugLine="End Sub";
Debug.ShouldStop(4096);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _globals() throws Exception{
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
try {
		Debug.PushSubsStack("initializeObjects (main) ","main",0,mostCurrent.activityBA,mostCurrent,48);
 BA.debugLineNum = 48;BA.debugLine="Sub initializeObjects";
Debug.ShouldStop(32768);
 BA.debugLineNum = 49;BA.debugLine="startAppSplash.initialize";
Debug.ShouldStop(65536);
mostCurrent._startappsplash.initialize(mostCurrent.activityBA);
 BA.debugLineNum = 50;BA.debugLine="startAppInterstitial.initialize";
Debug.ShouldStop(131072);
mostCurrent._startappinterstitial.initialize(mostCurrent.activityBA);
 BA.debugLineNum = 51;BA.debugLine="startAppRewardedVideo.initialize";
Debug.ShouldStop(262144);
mostCurrent._startapprewardedvideo.initialize(mostCurrent.activityBA);
 BA.debugLineNum = 52;BA.debugLine="End Sub";
Debug.ShouldStop(524288);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _initializesdk() throws Exception{
try {
		Debug.PushSubsStack("initializeSDK (main) ","main",0,mostCurrent.activityBA,mostCurrent,54);
 BA.debugLineNum = 54;BA.debugLine="Sub initializeSDK";
Debug.ShouldStop(2097152);
 BA.debugLineNum = 55;BA.debugLine="sdk.init(appID, enableReturnAds)";
Debug.ShouldStop(4194304);
mostCurrent._sdk.init(mostCurrent.activityBA,mostCurrent._appid,_enablereturnads);
 BA.debugLineNum = 56;BA.debugLine="End Sub";
Debug.ShouldStop(8388608);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _loadinterstitialad() throws Exception{
try {
		Debug.PushSubsStack("loadInterstitialAd (main) ","main",0,mostCurrent.activityBA,mostCurrent,68);
 BA.debugLineNum = 68;BA.debugLine="Sub loadInterstitialAd";
Debug.ShouldStop(8);
 BA.debugLineNum = 69;BA.debugLine="startAppInterstitial.loadAd";
Debug.ShouldStop(16);
mostCurrent._startappinterstitial.loadAd(mostCurrent.activityBA);
 BA.debugLineNum = 70;BA.debugLine="End Sub";
Debug.ShouldStop(32);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _loadrewardedvideo() throws Exception{
try {
		Debug.PushSubsStack("loadRewardedVideo (main) ","main",0,mostCurrent.activityBA,mostCurrent,78);
com.startapp.android.publish.b4a.StartAppAdWrapper.AdModeTypeWrapper _ad_mode = null;
 BA.debugLineNum = 78;BA.debugLine="Sub loadRewardedVideo";
Debug.ShouldStop(8192);
 BA.debugLineNum = 79;BA.debugLine="Dim ad_mode As AdMode";
Debug.ShouldStop(16384);
_ad_mode = new com.startapp.android.publish.b4a.StartAppAdWrapper.AdModeTypeWrapper();Debug.locals.put("ad_mode", _ad_mode);
 BA.debugLineNum = 80;BA.debugLine="startAppRewardedVideo.setVideoListener(\"VideoList";
Debug.ShouldStop(32768);
mostCurrent._startapprewardedvideo.setVideoListener(mostCurrent.activityBA,"VideoListener");
 BA.debugLineNum = 81;BA.debugLine="startAppRewardedVideo.loadAdWithAdMode(ad_mode.RE";
Debug.ShouldStop(65536);
mostCurrent._startapprewardedvideo.loadAdWithAdMode(mostCurrent.activityBA,_ad_mode.REWARDED_VIDEO);
 BA.debugLineNum = 82;BA.debugLine="End Sub";
Debug.ShouldStop(131072);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}

public static void initializeProcessGlobals() {
    if (mostCurrent != null) {
Debug.StartDebugging(mostCurrent.activityBA, 25350, new int[] {5}, "24a2e2f5-cf87-499f-b7c5-085cd9b5f682");}

    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="End Sub";
return "";
}
public static String  _showbanner() throws Exception{
try {
		Debug.PushSubsStack("showBanner (main) ","main",0,mostCurrent.activityBA,mostCurrent,72);
com.startapp.android.publish.b4a.banner.BannerWrapper _autobanner = null;
com.startapp.android.publish.b4a.banner.BannerWrapper.BannerPositionWrapper _bannerpositiontoset = null;
 BA.debugLineNum = 72;BA.debugLine="Sub showBanner";
Debug.ShouldStop(128);
 BA.debugLineNum = 73;BA.debugLine="Dim autoBanner As Banner";
Debug.ShouldStop(256);
_autobanner = new com.startapp.android.publish.b4a.banner.BannerWrapper();Debug.locals.put("autoBanner", _autobanner);
 BA.debugLineNum = 74;BA.debugLine="Dim bannerPositionToSet As BannerPosition";
Debug.ShouldStop(512);
_bannerpositiontoset = new com.startapp.android.publish.b4a.banner.BannerWrapper.BannerPositionWrapper();Debug.locals.put("bannerPositionToSet", _bannerpositiontoset);
 BA.debugLineNum = 75;BA.debugLine="autoBanner.addBannerWithPosition(bannerPositionTo";
Debug.ShouldStop(1024);
_autobanner.addBannerWithPosition(mostCurrent.activityBA,_bannerpositiontoset.BOTTOM);
 BA.debugLineNum = 76;BA.debugLine="End Sub";
Debug.ShouldStop(2048);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _showsplashad(boolean _firsttime) throws Exception{
try {
		Debug.PushSubsStack("showSplashAd (main) ","main",0,mostCurrent.activityBA,mostCurrent,58);
com.startapp.android.publish.b4a.splash.SplashConfigWrapper _splashconf = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _logotoset = null;
Debug.locals.put("FirstTime", _firsttime);
 BA.debugLineNum = 58;BA.debugLine="Sub showSplashAd(FirstTime As Boolean)";
Debug.ShouldStop(33554432);
 BA.debugLineNum = 59;BA.debugLine="Dim splashConf As SplashConfig";
Debug.ShouldStop(67108864);
_splashconf = new com.startapp.android.publish.b4a.splash.SplashConfigWrapper();Debug.locals.put("splashConf", _splashconf);
 BA.debugLineNum = 60;BA.debugLine="Dim logoToSet As Bitmap";
Debug.ShouldStop(134217728);
_logotoset = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();Debug.locals.put("logoToSet", _logotoset);
 BA.debugLineNum = 61;BA.debugLine="splashConf.initialize";
Debug.ShouldStop(268435456);
_splashconf.initialize(processBA);
 BA.debugLineNum = 62;BA.debugLine="splashConf.setAppName(\"B4A with StartApp 3.1.2\")";
Debug.ShouldStop(536870912);
_splashconf.setAppName(processBA,"B4A with StartApp 3.1.2");
 BA.debugLineNum = 63;BA.debugLine="logoToSet.initialize(File.DirAssets, \"logo.png\")";
Debug.ShouldStop(1073741824);
_logotoset.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"logo.png");
 BA.debugLineNum = 64;BA.debugLine="splashConf.setLogo(logoToSet)";
Debug.ShouldStop(-2147483648);
_splashconf.setLogo(processBA,(android.graphics.Bitmap)(_logotoset.getObject()));
 BA.debugLineNum = 65;BA.debugLine="startAppSplash.showSplashWithSplashConfig(FirstTi";
Debug.ShouldStop(1);
mostCurrent._startappsplash.showSplashWithSplashConfig(mostCurrent.activityBA,_firsttime,_splashconf);
 BA.debugLineNum = 66;BA.debugLine="End Sub";
Debug.ShouldStop(2);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _videolistener_videocompleted() throws Exception{
try {
		Debug.PushSubsStack("VideoListener_VideoCompleted (main) ","main",0,mostCurrent.activityBA,mostCurrent,135);
 BA.debugLineNum = 135;BA.debugLine="Sub VideoListener_VideoCompleted";
Debug.ShouldStop(64);
 BA.debugLineNum = 136;BA.debugLine="ToastMessageShow (\"Video Completed\", False)";
Debug.ShouldStop(128);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Video Completed",anywheresoftware.b4a.keywords.Common.False);
 BA.debugLineNum = 137;BA.debugLine="End Sub";
Debug.ShouldStop(256);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
}
