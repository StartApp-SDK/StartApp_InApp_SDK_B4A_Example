package b4a.example.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_activity_main{

public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
//BA.debugLineNum = 2;BA.debugLine="btnShowInterstitial.Top = 10%y"[activity_main/General script]
views.get("btnshowinterstitial").vw.setTop((int)((10d / 100 * height)));
//BA.debugLineNum = 3;BA.debugLine="btnShowInterstitial.Width = 100%x"[activity_main/General script]
views.get("btnshowinterstitial").vw.setWidth((int)((100d / 100 * width)));
//BA.debugLineNum = 4;BA.debugLine="btnShowInterstitial.Height = 22%y"[activity_main/General script]
views.get("btnshowinterstitial").vw.setHeight((int)((22d / 100 * height)));
//BA.debugLineNum = 5;BA.debugLine="btnShowInterstitial.Left = 0"[activity_main/General script]
views.get("btnshowinterstitial").vw.setLeft((int)(0d));
//BA.debugLineNum = 6;BA.debugLine="btnShowInterstitial.TextSize = 23"[activity_main/General script]
((anywheresoftware.b4a.keywords.LayoutBuilder.DesignerTextSizeMethod)views.get("btnshowinterstitial").vw).setTextSize((float)(23d));
//BA.debugLineNum = 8;BA.debugLine="btwShowInterstitialWithCallback.Top = btnShowInterstitial.Bottom"[activity_main/General script]
views.get("btwshowinterstitialwithcallback").vw.setTop((int)((views.get("btnshowinterstitial").vw.getTop() + views.get("btnshowinterstitial").vw.getHeight())));
//BA.debugLineNum = 9;BA.debugLine="btwShowInterstitialWithCallback.Width = 100%x"[activity_main/General script]
views.get("btwshowinterstitialwithcallback").vw.setWidth((int)((100d / 100 * width)));
//BA.debugLineNum = 10;BA.debugLine="btwShowInterstitialWithCallback.Height = 22%y"[activity_main/General script]
views.get("btwshowinterstitialwithcallback").vw.setHeight((int)((22d / 100 * height)));
//BA.debugLineNum = 11;BA.debugLine="btwShowInterstitialWithCallback.Left = btnShowInterstitial.Left"[activity_main/General script]
views.get("btwshowinterstitialwithcallback").vw.setLeft((int)((views.get("btnshowinterstitial").vw.getLeft())));
//BA.debugLineNum = 12;BA.debugLine="btwShowInterstitialWithCallback.TextSize = 23"[activity_main/General script]
((anywheresoftware.b4a.keywords.LayoutBuilder.DesignerTextSizeMethod)views.get("btwshowinterstitialwithcallback").vw).setTextSize((float)(23d));
//BA.debugLineNum = 14;BA.debugLine="btnShowRewardedVideo.Top = btwShowInterstitialWithCallback.Bottom"[activity_main/General script]
views.get("btnshowrewardedvideo").vw.setTop((int)((views.get("btwshowinterstitialwithcallback").vw.getTop() + views.get("btwshowinterstitialwithcallback").vw.getHeight())));
//BA.debugLineNum = 15;BA.debugLine="btnShowRewardedVideo.Width = 100%x"[activity_main/General script]
views.get("btnshowrewardedvideo").vw.setWidth((int)((100d / 100 * width)));
//BA.debugLineNum = 16;BA.debugLine="btnShowRewardedVideo.Height = 22%y"[activity_main/General script]
views.get("btnshowrewardedvideo").vw.setHeight((int)((22d / 100 * height)));
//BA.debugLineNum = 17;BA.debugLine="btnShowRewardedVideo.Left = btnShowInterstitial.Left"[activity_main/General script]
views.get("btnshowrewardedvideo").vw.setLeft((int)((views.get("btnshowinterstitial").vw.getLeft())));
//BA.debugLineNum = 18;BA.debugLine="btnShowRewardedVideo.TextSize = 23"[activity_main/General script]
((anywheresoftware.b4a.keywords.LayoutBuilder.DesignerTextSizeMethod)views.get("btnshowrewardedvideo").vw).setTextSize((float)(23d));

}
}