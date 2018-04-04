/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Trace;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Advanceable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.download.DownLoadService;
import com.android.download.DownLoadTaskInfo;
import com.android.gallery3d.util.LogUtils;
import com.android.launcher3.DragLayer.LayoutParams;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.PagedView.FLING_STATE;
import com.android.launcher3.Workspace.AlphaUpdateListener;
import com.android.launcher3.Workspace.ZoomInInterpolator;
import com.android.launcher3.anim.AbsAppsAnimation;
import com.android.launcher3.anim.FadeInAppsAnimation;
import com.android.launcher3.anim.FadeoutAnimation;
import com.android.launcher3.anim.RotateAppsAnimation;
import com.android.launcher3.anim.RotateXAppsAnimation;
import com.android.launcher3.anim.RotateYAppsAnimation;
import com.android.launcher3.anim.TransitionAnims.TransitionListener;
import com.android.launcher3.bean.Theme;
import com.android.launcher3.bean.Wallpaper;
import com.android.launcher3.explosion.ExplosionField;
import com.android.launcher3.lq.FindDefaultResoures;
import com.android.launcher3.nifty.NiftyObserables;
import com.android.launcher3.notify.PreferencesManager;
import com.android.launcher3.prefernce.LauncherPrefercensActivity;
import com.android.launcher3.tsearch.NumberView;
import com.android.launcher3.tsearch.SearchView;
import com.android.launcher3.view.AlignmentLinearLayout;
import com.android.launcher3.view.ExplosionView;
import com.android.launcher3.view.LauncherBackgroudView;
import com.android.launcher3.view.PrizeMultipleEditNagiration;
import com.android.launcher3.view.PrizeMultipleEditNagiration.MutipleEditState;
import com.android.launcher3.view.PrizeNavigationLayout;
import com.lqsoft.LqServiceUpdater.LqService;
import com.lqsoft.lqtheme.LqShredPreferences;
import com.lqsoft.lqtheme.LqtThemeParserAdapter;
import com.lqsoft.lqtheme.OLThemeNotification;
import com.mediatek.common.featureoption.FeatureOption;
import com.mediatek.launcher3.ext.LauncherLog;

/**
 * Default launcher application.
 */
// M by zhouerlong
/**
 * @author Administrator
 * 
 */
public class Launcher extends Activity implements
		View.OnClickListener, OnLongClickListener, LauncherModel.Callbacks,
		View.OnTouchListener ,
                   MTKUnreadLoader.UnreadCallbacks  ,SensorEventListener,TransitionListener{
	static final String TAG = "Launcher";
	static final String TAG_SURFACEWIDGET = "MTKWidgetView";
	static final boolean LOGD = false;

	static final boolean PROFILE_STARTUP = false;
	static final boolean DEBUG_WIDGETS = false;
	static final boolean DEBUG_STRICT_MODE = false;
	static final boolean DEBUG_RESUME_TIME = false;
	static final boolean DEBUG_DUMP_LOG = false;
	static final boolean isScrollWallpaper = false;
	public static boolean mNeedSwimming = false;
	public static  boolean mStartSwimming = false;
	public static boolean folderDecompression=false; //add by zel
	private static final String ACTION_DATE_CHANGED = "android.intent.action.TIME_SET";
	public static  int mCount = 0;
	private static final int SENSOR_VALUE = 18;
	/**
	 * 返回编辑模式状态
	 */
	public static int BACK_OVER_SPRING_ID=0;
	/**
	 * 进入编辑模式状态
	 */
	public static int FIRST_ENTER_OVER_SPRING_ID=1;

	private static final int REQUEST_CREATE_SHORTCUT = 1;
	private static final int REQUEST_CREATE_APPWIDGET = 5;
	private static final int REQUEST_PICK_APPLICATION = 6;
	private static final int REQUEST_PICK_SHORTCUT = 7;
	private static final int REQUEST_PICK_APPWIDGET = 9;
	private static final int REQUEST_PICK_WALLPAPER = 10;
	public static  int display_widht=0;
	public static  int display_height=0;
	
	public static boolean isSupportIconSize=true;
	
	public static boolean isSupportHideApp=false;
	public static boolean isSupportT9Search=false;
	
	

	private RadioGroup mOverViewTabsRadioGroup;// A b zel
	
	/**
	 * 搜素控件
	 */
	private SearchView mSearchView;
	/**
	 * 特效和进入主菜单动画单选组
	 */
//	private RadioGroup mEffectAndAppsAnimationRadioGroup;
	/**壁纸
	 * 
	 */
	private RadioButton mWallpaperRadioButton;
	ArrayList<ShortcutInfo> hides = new ArrayList<>();
	ArrayList<ShortcutInfo> visibles = new ArrayList<>();
	/**
	 * 特效
	 */
	private RadioButton mEffectsRadioButton;
	/**
	 * 小部件
	 */
	private RadioButton mWidgetRadioButton;
	/**
	 * 主题
	 */
	private RadioButton mThemeRadioButton;
	// private ImageView mDelImageView;//r by zel

	enum TAG {
		NORMAL, SPRING_LOADED, SMALL, OVERVIEW
	};// add by zhouerlong

	private static final int REQUEST_BIND_APPWIDGET = 11;

	private static final int MENU_GROUP_EDIT = 1;// A by zel
	// / M: request to hide application, add for OP09.
	private static final int REQUEST_HIDE_APPS = 12;
	private static final String HIDE_PACKAGE_NAME = "com.android.launcher3";
	private static final String HIDE_ACTIVITY_NAME = "com.android.launcher3.HideAppsActivity";

	/**
	 * 编辑模式下的一些View
	 */
	private HashMap<SpringState, View> mAllSpringViews = new HashMap<SpringState, View>();
	/**
	 * 桌面特效和进入主菜单动画 
	 * 如果是一级界面就没有主菜单动画这一项
	 */
	
	

	/**
	 * IntentStarter uses request codes starting with this. This must be greater
	 * than all activity request codes used internally.
	 */
	protected static final int REQUEST_LAST = 100;
	private boolean mIsOpenMenu = true; //??menu??

	static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";

	static final int SCREEN_COUNT = 5;
	static final int DEFAULT_SCREEN = 2;

    /**
     * 动画速率
     */
    private final DecelerateInterpolator mZoomInInterpolator = new DecelerateInterpolator();
    
	private static final String PREFERENCES = "launcher.preferences";
	// To turn on these properties, type
	// adb shell setprop log.tag.PROPERTY_NAME [VERBOSE | SUPPRESS]
	static final String FORCE_ENABLE_ROTATION_PROPERTY = "launcher_force_rotate";
	static final String DUMP_STATE_PROPERTY = "launcher_dump_state";

	// The Intent extra that defines whether to ignore the launch animation
	static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION = "com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION";

	// Type: int
	private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
	// Type: int
	private static final String RUNTIME_STATE = "launcher.state";
	// Type: int
	private static final String RUNTIME_STATE_PENDING_ADD_CONTAINER = "launcher.add_container";
	// Type: int
	private static final String RUNTIME_STATE_PENDING_ADD_SCREEN = "launcher.add_screen";
	// Type: int
	private static final String RUNTIME_STATE_PENDING_ADD_CELL_X = "launcher.add_cell_x";
	// Type: int
	private static final String RUNTIME_STATE_PENDING_ADD_CELL_Y = "launcher.add_cell_y";
	// Type: boolean
	private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME = "launcher.rename_folder";
	// Type: long
	private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME_ID = "launcher.rename_folder_id";
	// Type: int
	private static final String RUNTIME_STATE_PENDING_ADD_SPAN_X = "launcher.add_span_x";
	// Type: int
	private static final String RUNTIME_STATE_PENDING_ADD_SPAN_Y = "launcher.add_span_y";
	// Type: parcelable
	private static final String RUNTIME_STATE_PENDING_ADD_WIDGET_INFO = "launcher.add_widget_info";

	private static final String TOOLBAR_ICON_METADATA_NAME = "com.android.launcher.toolbar_icon";
	private static final String TOOLBAR_SEARCH_ICON_METADATA_NAME = "com.android.launcher.toolbar_search_icon";
	private static final String TOOLBAR_VOICE_SEARCH_ICON_METADATA_NAME = "com.android.launcher.toolbar_voice_search_icon";

	public static final String SHOW_WEIGHT_WATCHER = "debug.show_mem";
	public static final boolean SHOW_WEIGHT_WATCHER_DEFAULT = false;
	
	/** The different states that Launcher can be in. */
	private enum State {
		NONE, WORKSPACE, APPS_CUSTOMIZE, APPS_CUSTOMIZE_SPRING_LOADED
	};

	/**
	 * 编辑模式下的一些菜单项 
	 * WALLPAPER
	 * WIDGET
	 * APPS
	 * THEMES
	 * BATCH_EDIT_APPS 增加了批量处理功能
	 * @author Administrator
	 *
	 */
	public enum SpringState {
		NONE, WALLPAPER, WIDGET, APPS_EDIT, THEMES, ANIM_EFFECT, APPS,BATCH_EDIT_APPS// add by
																		// zel
	};// M by zhouerlong
		// add by zhouerlong
	private enum EditMode {
		ENTER_EDIT,NORMAL_EDIT;
	}
	
	private EditMode mIsEnterEdit = EditMode.NORMAL_EDIT;
	private enum EditState {
		NORMAL, SPRING_LOAD
	};// M by zhouerlong
	
	public static enum IconChangeState {
		DEL, EDIT,NORMAL
	};// M by zhouerlong
	/**
	 * 三种状态 退出状态，编辑状态，卸载状态
	 */
	private IconChangeState mIconState=IconChangeState.NORMAL;
	public IconChangeState getIconState() {
		return mIconState;
	}
	public void setIconState(IconChangeState mIconState) {
		this.mIconState = mIconState;
	}
	/**
	 * 特效和动画
	 * @author Administrator
	 *
	 */
	private enum EffectState {
		EFFECTS,ANIMS,NONE
	}

	private State mState = State.WORKSPACE;
	private SpringState mSpringState = SpringState.NONE;// M by zhouerlong
	private EffectState mEffectState  = EffectState.NONE;// M by zhouerlong
	
	private AnimatorSet mStateAnimation;

	static final int APPWIDGET_HOST_ID = 1024;
	private static final int EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT = 300;
	private static final int EXIT_SPRINGLOADED_MODE_LONG_TIMEOUT = 600;
	private static final int SHOW_CLING_DURATION = 250;
	private static final int DISMISS_CLING_DURATION = 200;

	private static final Object sLock = new Object();
	private static int sScreen = DEFAULT_SCREEN;

	// How long to wait before the new-shortcut animation automatically pans the
	// workspace
	private static int NEW_APPS_PAGE_MOVE_DELAY = 500;
	private static int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 5;
	private static int NEW_APPS_ANIMATION_DELAY = 500;

	private final BroadcastReceiver mCloseSystemDialogsReceiver = new CloseSystemDialogsIntentReceiver();
	private final ContentObserver mWidgetObserver = new AppWidgetResetObserver();

	private LayoutInflater mInflater;

	private Workspace mWorkspace;
	private SensorManager mSensorManager = null;
	
	//add by zhouerlong 20150808 begin
	/**
	 * 创建被观测者
	 */
	private NiftyObserables mNifObserables = new NiftyObserables();
	
	/**
	 * @return
	 */
	public NiftyObserables getmNifObserables() {
		return mNifObserables;
	}
	/**
	 * @param mNifObserables
	 */
	public void setmNifObserables(NiftyObserables mNifObserables) {
		this.mNifObserables = mNifObserables;
	}
	//add by zhouerlong 20150808 end

	private View mLauncherView;
	private LauncherBackgroudView mLauncherBackground;// M by xxf
	public LauncherBackgroudView getLauncherBackground() {
		return mLauncherBackground;
	}

	private LauncherBackgroudView mWallpaperBg;// M by xxf
	public LauncherBackgroudView getWallpaperBg() {
		return mWallpaperBg;
	}
	public void enterDeleteDropTarget(boolean enterOrOut) {
        View dockView = getworkspace().isInSpringLoadMoed()?getOverviewPanel():getHotseat();
		if (enterOrOut) {
				mLauncherBackground.setAlpha(1f);
				mLauncherBackground.setVisibility(View.VISIBLE);
				mWallpaperBg.setAlpha(1f);
				mWallpaperBg.setVisibility(View.VISIBLE);
				mPageIndicators.setAlpha(0f);
				getworkspace().setAlpha(0f);
				dockView.setAlpha(0f);
				Rect r = new Rect();
				getSearchBar().getmDeleteDropTarget().getHitRectRelativeToDragLayer(r);
				mHeight = r.height();
				if (mTempProgress==0) {
			        mProgress = mTempProgress = 0f;
				}
				dockView.setVisibility(View.GONE);
				getworkspace().setVisibility(View.GONE);
		} else {

				mLauncherBackground.setAlpha(0f);
				mWallpaperBg.setAlpha(0f);
				mPageIndicators.setAlpha(1f);
				getworkspace().setAlpha(1f);
				dockView.setAlpha(1f);
				mWallpaperBg.setVisibility(View.GONE);
					dockView.setVisibility(View.VISIBLE);
				getworkspace().setVisibility(View.VISIBLE);
				mLauncherBackground.setVisibility(View.GONE);
		}
	}
	public void setmLauncherBackground(LauncherBackgroudView mLauncherBackground) {
		this.mLauncherBackground = mLauncherBackground;
	}

	private ExplosionView mExplosionDialogView;
	private DragLayer mDragLayer;
	private DragController mDragController;
	private View mWeightWatcher;

	private AppWidgetManager mAppWidgetManager;
	private LauncherAppWidgetHost mAppWidgetHost;

	private ItemInfo mPendingAddInfo = new ItemInfo();
	private AppWidgetProviderInfo mPendingAddWidgetInfo;

	private int[] mTmpAddItemCellCoordinates = new int[2];

	private FolderInfo mFolderInfo;

	private Hotseat mHotseat;
	/**
	 * 编辑模式主界面
	 */
	private View mOverviewPanel;

	private View mAllAppsButton;

	private SearchDropTargetBar mSearchDropTargetBar;

	private HideAppView mHideView;
	private VisibleAppsView mVisibleView;
	/**
	 * 小部件布局
	 */
	private AppsCustomizePagedView mAppsCustomizeContentSpringWidget;// M by
																		// zhouerlong
	/**
	 * 编辑模式下 Apps布局
	 */
/*	private AppsCustomizePagedView mAppsCustomizeContentSpringApps;// M by
																	// zhouerlong
*/
	/**
	 * 批量处理 oppo风格用到的空间
	 */
	private AlignmentLinearLayout mAlignmentLinearLayout;// M by没有
															// zh ouerlong
	
	/**
	 * 顶部显示批量功能控件
	 */
	private HideAppsView mHideAppsView;
	/**
	 * 主题布局
	 */
	private ThemeListView mThemeListView;// m by zhouerlong 没有隐藏
	/**
	 * 主题布局
	 */
	private WallpaperListView mWalListView;// m by zhouerlong 没有隐藏

	/**
	 * 特效布局
	 */
	private EffectLinearLayout mEffectLayoutView;// m by zhouerlong没有
	/**
	 * 这里定义这些空间是为了解决一个buge而设立的 
	 * 解决：切换tab的时候出现无法进入子目录焦点错误
	 * begin
	 */
	private View mThemesParent;// m by zhouerlong
	private View mWallpaperParent;// m by zhouerlong
	private View mEffectParent;
	private View mAnimationParent;
//	private View mEffectsTabView;// m by zhouerlong
	/**
	 * 这里定义这些空间是为了解决一个buge而设立的 
	 * 解决：切换tab的时候出现无法进入子目录焦点错误
	 * end
	 */
//	private View mWallpaper;// M by zhouerlong
	private boolean mAutoAdvanceRunning = false;
	private View mQsbBar;

	private Bundle mSavedState;
	// We set the state in both onCreate and then onNewIntent in some cases,
	// which causes both
	// scroll issues (because the workspace may not have been measured yet) and
	// extra work.
	// Instead, just save the state that we need to restore Launcher to, and
	// commit it in onResume.
	private State mOnResumeState = State.NONE;

	private SpannableStringBuilder mDefaultKeySsb = null;

	private boolean mWorkspaceLoading = true;

	private boolean mPaused = true;
	/**
	 * oppo MI 风格开关 
	 * true 为 MIUI
	 * false 为OPPOUI
	 */
	static public boolean style = false;
	private boolean mRestoring;
	private boolean mWaitingForResult;
	private boolean mOnResumeNeedsLoad;

	private ArrayList<Runnable> mBindOnResumeCallbacks = new ArrayList<Runnable>();
	private ArrayList<Runnable> mOnResumeCallbacks = new ArrayList<Runnable>();

	// Keep track of whether the user has left launcher
	private static boolean sPausedFromUserAction = false;

	private Bundle mSavedInstanceState;

	private LauncherModel mModel;
	private IconCache mIconCache;
	private boolean mUserPresent = true;
	private boolean mVisible = false;
	private boolean mHasFocus = false;
	private boolean mAttached = false;
	//add by zhouerlong 0728 begin
	private ComponentName mCalendarComponentName=new ComponentName("com.android.calendar",
			"com.android.calendar.AllInOneActivity");
	//add by zhouerlong 0728 end
	private static final boolean DISABLE_CLINGS = false;
	private static final boolean DISABLE_CUSTOM_CLINGS = true;

	private static LocaleConfiguration sLocaleConfiguration = null;

	private static HashMap<Long, FolderInfo> sFolders =   new  HashMap<Long, FolderInfo>();

	private View.OnTouchListener mHapticFeedbackTouchListener;

	// Related to the auto-advancing of widgets
	private final int ADVANCE_MSG = 1;
	private final int mAdvanceInterval = 20000;
	private final int mAdvanceStagger = 250;
	private long mAutoAdvanceSentTime;
	private long mAutoAdvanceTimeLeft = -1;
	private HashMap<View, AppWidgetProviderInfo> mWidgetsToAdvance = new HashMap<View, AppWidgetProviderInfo>();
	
//add by zhouerlong
//    private static final int LOW_BATTERY_LEVEL_THRESHOLD_FOR_FINISH = 100;
    private int LOW_BATTERY_LEVEL_THRESHOLD =5 ;
    private BatteryBroadcastReceiver mBatteryReceiver = new BatteryBroadcastReceiver();
    private boolean mIsLowBattery = false;
	//add by zhouerlong

	// Determines how long to wait after a rotation before restoring the screen
	// orientation to
	// match the sensor state.
	private final int mRestoreScreenOrientationDelay = 500;

	// External icons saved in case of resource changes, orientation, etc.
	private static Drawable.ConstantState[] sGlobalSearchIcon = new Drawable.ConstantState[2];
	private static Drawable.ConstantState[] sVoiceSearchIcon = new Drawable.ConstantState[2];
	private static Drawable.ConstantState[] sAppMarketIcon = new Drawable.ConstantState[2];

	private Intent mAppMarketIntent = null;
	private static final boolean DISABLE_MARKET_BUTTON = true;


	private final ArrayList<Integer> mSynchronouslyBoundPages = new ArrayList<Integer>();

	static final ArrayList<String> sDumpLogs = new ArrayList<String>();
	static Date sDateStamp = new Date();
	static DateFormat sDateFormat = DateFormat.getDateTimeInstance(
			DateFormat.SHORT, DateFormat.SHORT);
	static long sRunStart = System.currentTimeMillis();
	static final String CORRUPTION_EMAIL_SENT_KEY = "corruptionEmailSent";

	// We only want to get the SharedPreferences once since it does an FS stat
	// each time we get
	// it from the context.
	private SharedPreferences mSharedPrefs;

	private static ArrayList<ComponentName> mIntentsOnWorkspaceFromUpgradePath = null;

	// Holds the page that we need to animate to, and the icon views that we
	// need to animate up
	// when we scroll to that page on resume.
	private ImageView mFolderIconImageView;
	private Bitmap mFolderIconBitmap;
	private Canvas mFolderIconCanvas;
	public ImageView mStripEmptyScreenIcon = null;
	// add by zhouerlong
	private Rect mRectForFolderAnimation = new Rect();

	private BubbleTextView mWaitingForResume;

	// added by yhf
	public static int viewHeight;
	public static int textSize;
	public static int viewWidth;
	/*prize-uninstall-xiaxuefeng-2015-8-3-start*/
	private final int UNINSTALL_COMPLETE = 2;
	final static int SUCCEEDED = 1;
	final static boolean UNINSTALL_SILENT = true;//静默卸载开关
	/*prize-uninstall-xiaxuefeng-2015-8-3-end*/
	private HideFromAccessibilityHelper mHideFromAccessibilityHelper = new HideFromAccessibilityHelper();

	private Runnable mBuildLayersRunnable = new Runnable() {
		public void run() {
			if (mWorkspace != null) {
				mWorkspace.buildPageHardwareLayers();
			}
		}
	};
//add by xxf	
	void hideWorkspaceSearchAndHotseat() {
        if (mWorkspace != null) mWorkspace.setAlpha(0f);

        View dockView = getworkspace().isInSpringLoadMoed()?getOverviewPanel():getHotseat();
        if (dockView != null) dockView.setAlpha(0f);
        View navirationView = getWorkspace().isInDragModed()?mNavigationLayout:null;
        View mulEditNagiration = getWorkspace().isInDragModed()?mMulEditNagiration:null;
        if (mulEditNagiration != null) mulEditNagiration.setAlpha(0f);
        if (navirationView != null) navirationView.setAlpha(0f);
        if (mPageIndicators != null) mPageIndicators.setAlpha(0f);
    }
//add by xxf	
    void showWorkspaceSearchAndHotseat() {
        if (mWorkspace != null) mWorkspace.setAlpha(1f);
        View dockView = getworkspace().isInSpringLoadMoed()?getOverviewPanel():getHotseat();
        if (dockView != null) dockView.setAlpha(1f);
        View navirationView = getWorkspace().isInDragModed()?mNavigationLayout:null;
        View mulEditNagiration = getWorkspace().isInDragModed()?mMulEditNagiration:null;
        if (mulEditNagiration != null) mulEditNagiration.setAlpha(1f);
        if (navirationView != null) navirationView.setAlpha(1f);
        if (mPageIndicators != null) mPageIndicators.setAlpha(1f);
        if (mDragController.isDragging()) {
        	setTraslationYByBlueBg();
        }
        
    }	
    
//add by zhouerlong
    private void registerBatteryReceiver() {
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, batteryFilter);
    }

    public void showToast(int stringId) {
        String message = getString(stringId);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void unregisterBatteryReceiver() {
    	this.unregisterReceiver(mBatteryReceiver);
    }
	private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra("level", 0);

                if (level < LOW_BATTERY_LEVEL_THRESHOLD) {
                    mIsLowBattery = true;
                } else {
                    mIsLowBattery = false;
                }
            }
        }
    }//add by zhouerlong
	private static ArrayList<PendingAddArguments> sPendingAddList = new ArrayList<PendingAddArguments>();

	public static boolean sForceEnableRotation = isPropertyEnabled(FORCE_ENABLE_ROTATION_PROPERTY);

	private static class PendingAddArguments {
		int requestCode;
		Intent intent;
		long container;
		long screenId;
		int cellX;
		int cellY;
	}

	private Stats mStats;

	private static boolean isPropertyEnabled(String propertyName) {
		return Log.isLoggable(propertyName, Log.VERBOSE);
	}

	// / M: Static variable to record whether locale has been changed.
	private static boolean sLocaleChanged = false;

	// / M: Add for launch specified applications in landscape. @{
	private static final int ORIENTATION_0 = 0;

	private OrientationEventListener mOrientationListener;
	// / @}

	// / M: Add for launcher unread shortcut feature. @{
	public static final int MAX_UNREAD_COUNT = 99;
	// add by zhouerlong

	private boolean mUnreadLoadCompleted = false;
	private boolean mBindingWorkspaceFinished = false;
	private boolean mBindingAppsFinished = false;
	// / @}

	// / M: Save current CellLayout bounds before
	// workspace.changeState(CellLayout will be scaled).
	private Rect mCurrentBounds = new Rect();

	// / M: Used to popup long press widget to add message.
	private Toast mLongPressWidgetToAddToast;

	// / M: Used to force reload when loading workspace
	private boolean mIsLoadingWorkspace;

	// / M: flag to indicate whether the orientation has changed.
	private boolean mOrientationChanged;

	// / M: flag to indicate whether the pages in app customized pane were
	// recreated.
	private boolean mPagesWereRecreated;

	// / M: whether the apps customize pane is in edit mode, add for OP09.
	private static boolean sIsInEditMode = false;

	private boolean mSupportEditAndHideApps = false;
	private ArrayList<AbsAppsAnimation> mAppsAnimations;//add by zel
	private View mPageIndicators;//add by xxf
	
	/**
	 * 导航容器
	 */
	private PrizeNavigationLayout  mNavigationLayout; 
	
	/**
	 * 批处理提示标题
	 */
	private PrizeMultipleEditNagiration mMulEditNagiration;

	public PrizeMultipleEditNagiration getMulEditNagiration() {
		return mMulEditNagiration;
	}

	private boolean isOpenSwitch=true; //???????
//	private ThemeInfo mThemeInfo = new ThemeInfo();
	// add by zhouerlong
	// / M: whether to use the pending apps queue to block all package
	// / add/update/removed events.
	private static boolean sUsePendingAppsQueue = false;

	// / M: list used to store all pending add/update/removed applications.
	private static ArrayList<PendingChangedApplications> sPendingChangedApps = new ArrayList<PendingChangedApplications>();
	
    /// M: Add for unread message feature.
    private MTKUnreadLoader mUnreadLoader = null;
    private List<String > mInterceptShortcuts = new ArrayList<>();
	private ExplosionField mExplosionField;
	public static  long   time;
	
	
//add by zel
	/**
	 * 初始化特效实例
	 */
	public void initAppsAnimations() {
		mAppsAnimations = new ArrayList<AbsAppsAnimation>();
		mAppsAnimations.add(new FadeInAppsAnimation());//默认
		mAppsAnimations.add(new RotateAppsAnimation());//漩涡
		mAppsAnimations.add(new RotateYAppsAnimation());//y旋转
		mAppsAnimations.add(new RotateXAppsAnimation());//x旋转
		mAppsAnimations.add(new FadeoutAnimation());//从小到大

	}

	public void setSearchViewAndLauncherAlpla(float scrollProgress) {
		float alpha = Math.abs(scrollProgress);
		float launcherAlpha = 1-alpha;
		mSearchView.setAlpha(alpha);
		mDragLayer.setAlpha(launcherAlpha);
		mWorkspace.setAlpha(launcherAlpha);
		mHotseat.setAlpha(launcherAlpha);
		
	}
	public AbsAppsAnimation  getChangeAppsAnimation() {
		
		int id = PreferencesManager.getCurrentAnimSelect(this);
		if (this.mAppsAnimations.size()== id) {
			Random r = new Random();
			id =  Math.abs(r.nextInt()%this.mAppsAnimations.size());
		}
		return mAppsAnimations.get(id);
	}
//add by zel
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (DEBUG_STRICT_MODE) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() 
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}
		
		if (AppsCustomizePagedView.LAUNCHER_STYLE.equals(AppsCustomizePagedView.LauncherStyle.MI)) {
			AppsCustomizePagedView.DISABLE_ALL_APPS=true;
		}else if (AppsCustomizePagedView.LAUNCHER_STYLE.equals(AppsCustomizePagedView.LauncherStyle.OPPO)){

			AppsCustomizePagedView.DISABLE_ALL_APPS=false;
		}
		
		LauncherAppState.setApplicationContext(getApplicationContext());
		LauncherAppState app = LauncherAppState.getInstance();
		textSize = this.getResources().getDimensionPixelSize(R.dimen.text_size);

		// / M: Add for smart book feature. Change DB if UI layout is changed.
		boolean isDatabaseIdChanged = false;
		if (FeatureOption.MTK_SMARTBOOK_SUPPORT) {
			int newDatabaseId = getResources()
					.getInteger(R.integer.database_id);
			if (LauncherAppState.getLauncherProvider().getDatabaseId() != newDatabaseId) {
				isDatabaseIdChanged = true;
				app.updateScreenInfo();
				app.getIconCache().flush();
				Utilities.initStatics(this);
				LauncherAppState.getLauncherProvider().setDatabaseId(newDatabaseId);
			}
		}

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);  
//		ThemeInfoUtils.dochangePkg(this, PreferencesManager.getCurrentTheme(this));

		initAppsAnimations();//add by zel
		// Determine the dynamic grid properties
		Point smallestSize = new Point();
		Point largestSize = new Point();
		Point realSize = new Point();
		Display display = getWindowManager().getDefaultDisplay();

		display = getWindowManager().getDefaultDisplay();
		viewWidth = display.getWidth();
		viewHeight = display.getHeight();
		display.getCurrentSizeRange(smallestSize, largestSize);
		display.getRealSize(realSize);
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		// Lazy-initialize the dynamic grid
		// / M: Add for smart book feature. Force re-init dynamic grid if
		// database ID is changed.
		DeviceProfile grid = app.initDynamicGrid(this,
				Math.min(smallestSize.x, smallestSize.y),
				Math.min(largestSize.x, largestSize.y), realSize.x, realSize.y,
				dm.widthPixels, dm.heightPixels, isDatabaseIdChanged);
		display_height = dm.heightPixels;
		display_widht = dm.widthPixels;

		// the LauncherApplication should call this, but in case of
		// Instrumentation it might not be present yet
		mSharedPrefs = getSharedPreferences(
				LauncherAppState.getSharedPreferencesKey(),
				Context.MODE_PRIVATE);
		mModel = app.setLauncher(this);
        /**M: added for unread feature, load and bind unread info.@{**/
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "R.bool.config_unreadSupport = "+getResources().getBoolean(R.bool.config_unreadSupport));
        }
        if (getResources().getBoolean(R.bool.config_unreadSupport)) {
            mUnreadLoader = LauncherAppState.getInstance().getLauncehrApplication().getUnreadLoader();
            mUnreadLoader.loadAndInitUnreadShortcuts();
            mUnreadLoader.initialize(this);
        }

		mIconCache = app.getIconCache();
		mIconCache.flushInvalidIcons(grid);
		mDragController = new DragController(this);
		mInflater = getLayoutInflater();


		Intent intent = new Intent();
		intent.setAction("com.sensortek.broadcast.enable");
		sendBroadcast(intent); 
		

		Intent bindService = new Intent();
		bindService.setAction("prize_appcenter_download_bind_service");
		sendBroadcast(bindService); 
		
		registerBatteryReceiver();//add by zhouerlong
		// end yhf
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "(Launcher)onCreate: savedInstanceState = "
					+ savedInstanceState + ", mModel = " + mModel
					+ ", mIconCache = " + mIconCache + ", this = " + this
					+ ", sLocaleChanged = " + sLocaleChanged);
		}

		mStats = new Stats(this);

		mAppWidgetManager = AppWidgetManager.getInstance(this);

		mAppWidgetHost = new LauncherAppWidgetHost(this, APPWIDGET_HOST_ID);
		mAppWidgetHost.startListening();

		// If we are getting an onCreate, we can actually preempt onResume and
		// unset mPaused here,
		// this also ensures that any synchronous binding below doesn't
		// re-trigger another
		// LauncherModel load.
		mPaused = false;

		if (PROFILE_STARTUP) {
			android.os.Debug.startMethodTracing(Environment
					.getExternalStorageDirectory() + "/launcher");
		}

		checkForLocaleChange();
		setContentView(R.layout.launcher);
		if (style) {
			mOverviewPanel = findViewById(R.id.overview_panel_mi);// 编辑面板

			View temp = findViewById(R.id.overview_panel);// 编辑面板
			ViewGroup  parent =(ViewGroup) temp.getParent();
			parent.removeView(temp);
			
		}else {
			mOverviewPanel = findViewById(R.id.overview_panel);// 编辑面板

			View temp = findViewById(R.id.overview_panel_mi);// 编辑面板
			ViewGroup  parent =(ViewGroup) temp.getParent();
			parent.removeView(temp);
		}
		super.onCreate(savedInstanceState);
		// M by zhouerlong

		setupViews();
		grid.layout(this);

		registerContentObservers();

		lockAllApps();
		loadIntercept();

		mSavedState = savedInstanceState;
		restoreState(mSavedState);

		// Update customization drawer _after_ restoring the states
		/*if (mAppsCustomizeContent != null) {
			mAppsCustomizeContent.onPackagesUpdated(LauncherModel
					.getSortedWidgetsAndShortcuts(this));
		}*/
		// add by zhouerlong
		if (this.mAppsCustomizeContentSpringWidget != null) {
			mAppsCustomizeContentSpringWidget.onPackagesUpdated(LauncherModel
					.getSortedWidgetsAndShortcuts(this));

		}// add by zhouerlong
			// add by zhouerlong
		/*if (this.mAppsCustomizeContentSpringApps != null) {
			mAppsCustomizeContentSpringApps.onPackagesUpdated(LauncherModel
					.getSortedWidgetsAndShortcuts(this));
		}*/
		// add by zhouerlong

		if (PROFILE_STARTUP) {
			android.os.Debug.stopMethodTracing();
		}

		if (!mRestoring) {
			// / M: Add for smart book feature. Reset load state if database
			// changed before.
			if (isDatabaseIdChanged) {
				mModel.resetLoadedState(true, true);
			}

			if (sPausedFromUserAction) {
				// If the user leaves launcher, then we should just load items
				// asynchronously when
				// they return.
				mModel.startLoader(true, -1);
			} else {
				// We only load the page synchronously if the user rotates (or
				// triggers a
				// configuration change) while launcher is in the foreground
				mModel.startLoader(true, mWorkspace.getCurrentPage());
			}
		}

		// For handling default keys
		mDefaultKeySsb = new SpannableStringBuilder();
		Selection.setSelection(mDefaultKeySsb, 0);

		IntentFilter filter = new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(mCloseSystemDialogsReceiver, filter);

		updateGlobalIcons();

		// On large interfaces, we want the screen to auto-rotate based on the
		// current orientation
		unlockScreenOrientation(true);

		//showFirstRunCling();
	}

	
	private void  loadIntercept() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					XmlParser.parseIntercepts(
							"intercept/default_intercept.xml", Launcher.this,mInterceptShortcuts);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		t.start();
	}

	// A by zel
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if (mIsOpenMenu) {

			Intent manageApps = new Intent(
					Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
			manageApps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			MenuInflater inflater = getMenuInflater();
			menu.add(0, 0, 0, R.string.menu_manage_apps)
					.setIcon(android.R.drawable.ic_menu_manage)
					.setIntent(manageApps);
			
			inflater.inflate(R.menu.launcher_option, menu);
		}
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);
		boolean allAppsVisible = false;//(mAppsCustomizeTabHost.getVisibility() == View.VISIBLE);
		
		
		if (mIsOpenMenu) {
		//add by zel workspace 不显示 menu
			if (allAppsVisible) {
				menu.findItem(R.id.menu_edit).setVisible(allAppsVisible);
				menu.findItem(0).setVisible(allAppsVisible);
				menu.findItem(R.id.menu_settings).setVisible(allAppsVisible);
				if (isOpenSwitch) {
					menu.findItem(R.id.launcher_settings).setVisible(allAppsVisible);
				}else {
					menu.findItem(R.id.launcher_settings).setVisible(!allAppsVisible);
				}
			}else {
				menu.findItem(R.id.menu_edit).setVisible(allAppsVisible);
				menu.findItem(0).setVisible(allAppsVisible);
				menu.findItem(R.id.menu_settings).setVisible(allAppsVisible);
				menu.findItem(R.id.launcher_settings).setVisible(allAppsVisible);
				
			}
//		} /*else {*/
			 if (mWorkspace.getState() == Workspace.State.DRAG_MODEL) {
					mWorkspace.exitDragMode(true, false);
					return true;
				}
			if (!allAppsVisible) {
				if ( !mWorkspace.isInSpringLoadMoed()/*&&!mWorkspace.isEnterSearchModel()*//*&&!mHotseat.isEnterSearchModel()*/) {

					if (mWorkspace.getOpenFolder() != null) {
						Folder openFolder = mWorkspace.getOpenFolder();
						if (openFolder.isEditingName()) {
							openFolder.dismissEditingName();
						}else if (mDragLayer.indexOfChild(mHideView)!=-1){
			            		 //如果当前 编辑文件夹的状态 先关闭 编辑文件夹界面
							mHideView.animateClosed();
							
						} else {
							closeFolder();
						}
					}else {

				         time = SystemClock.uptimeMillis();
				         if (mWorkspace.getState() != Workspace.State.DRAG_MODEL) {

				     		if(mExplosionDialogView.isOpen()) {
				     			mExplosionDialogView.animateClosed();
				     			return true;
				     		}
								mWorkspace.enterSprindLoadMode(this.getCurrrentSpringState());// 进入编辑模式
//								openHideApps(new Rect());
				         }
				         
					}
				}else {

					mWorkspace.onBackpressed();
					mWorkspace.exitSpringLoadMode(true,false);
				}
				// //暂且关闭
				// 此开关
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			this.startSettings();
			break;
		case R.id.menu_edit:
			boolean allAppsVisible =false;// (mAppsCustomizeTabHost.getVisibility() == View.VISIBLE);
			// A by zel
			if (!allAppsVisible) {
				if ( !mWorkspace.isInSpringLoadMoed()) {

					mWorkspace.enterSprindLoadMode(this.getCurrrentSpringState());// 进入编辑模式
				}else {
					mWorkspace.exitSpringLoadMode(true,false);
				}
			}								
			break;
		/*
		 * case R.id.clean_cache: DataCleanManager.cleanApplicationData(this);
		 */
		case R.id.launcher_settings:
			setupLauncherPreferences();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void setupLauncherPreferences() {
		this.startActivity(new Intent(this,LauncherPrefercensActivity.class));
	}
	
	
	/**启动oppo风格批处理
	 * @param v
	 */
	public void setupBatchEditModel(View v) {
		setupChangeAnimation(SpringState.BATCH_EDIT_APPS, 200, v.getId());
	}

	// A by zel
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
//		sPausedFromUserAction = true;
	}

	/**
	 * To be overriden by subclasses to hint to Launcher that we have custom
	 * content
	 */
	protected boolean hasCustomContentToLeft() {
		return false;
	}
	
	
	public SpringState getSpringState() {
		return mSpringState;
	}

	/**
	 * To be overridden by subclasses to create the custom content and call
	 * {@link #addToCustomContentPage}. This will only be invoked if
	 * {@link #hasCustomContentToLeft()} is {@code true}.
	 */
	protected void addCustomContentToLeft() {
	}

	/**
	 * Invoked by subclasses to signal a change to the
	 * {@link #addCustomContentToLeft} value to ensure the custom content page
	 * is added or removed if necessary.
	 */
	protected void invalidateHasCustomContentToLeft() {
		if (mWorkspace == null || mWorkspace.getScreenOrder().isEmpty()) {
			// Not bound yet, wait for bindScreens to be called.
			return;
		}

		if (!mWorkspace.hasCustomContent() && hasCustomContentToLeft()) {
			// Create the custom content page and call the subclass to populate
			// it.
			mWorkspace.createCustomContentPage();
			addCustomContentToLeft();
		} else if (mWorkspace.hasCustomContent() && !hasCustomContentToLeft()) {
			mWorkspace.removeCustomContentPage();
		}
	}
	
	public Workspace getworkspace() {
		return mWorkspace;
	}

	private void updateGlobalIcons() {
		boolean searchVisible = false;
		boolean voiceVisible = false;

		// If we have a saved version of these external icons, we load them up
		// immediately
		int coi = getCurrentOrientationIndexForGlobalIcons();
		if (sGlobalSearchIcon[coi] == null || sVoiceSearchIcon[coi] == null
				|| sAppMarketIcon[coi] == null) {
			if (!DISABLE_MARKET_BUTTON) {
				updateAppMarketIcon();
			}
			/*searchVisible = updateGlobalSearchIcon();
			voiceVisible = updateVoiceSearchIcon(searchVisible);*/
		}
		if (sGlobalSearchIcon[coi] != null) {
			updateGlobalSearchIcon(sGlobalSearchIcon[coi]);
			searchVisible = true;
		}
		if (sVoiceSearchIcon[coi] != null) {
			updateVoiceSearchIcon(sVoiceSearchIcon[coi]);
			voiceVisible = true;
		}
		if (!DISABLE_MARKET_BUTTON && sAppMarketIcon[coi] != null) {
			updateAppMarketIcon(sAppMarketIcon[coi]);
		}
		if (mSearchDropTargetBar != null) {
			mSearchDropTargetBar.onSearchPackagesChanged(searchVisible,
					voiceVisible);
		}
	}

	private void checkForLocaleChange() {
		if (sLocaleConfiguration == null) {
			new AsyncTask<Void, Void, LocaleConfiguration>() {
				@Override
				protected LocaleConfiguration doInBackground(Void... unused) {
					LocaleConfiguration localeConfiguration = new LocaleConfiguration();
					readConfiguration(Launcher.this, localeConfiguration);
					return localeConfiguration;
				}

				@Override
				protected void onPostExecute(LocaleConfiguration result) {
					sLocaleConfiguration = result;
					checkForLocaleChange(); // recursive, but now with a locale
											// configuration
				}
			}.execute();
			return;
		}

		final Configuration configuration = getResources().getConfiguration();

		final String previousLocale = sLocaleConfiguration.locale;
		final String locale = configuration.locale.toString();

		final int previousMcc = sLocaleConfiguration.mcc;
		final int mcc = configuration.mcc;

		final int previousMnc = sLocaleConfiguration.mnc;
		final int mnc = configuration.mnc;

		boolean localeChanged = !locale.equals(previousLocale)
				|| mcc != previousMcc || mnc != previousMnc;

		if (LauncherLog.DEBUG) {
			LauncherLog
					.d(TAG, "checkForLocaleChange: previousLocale = "
							+ previousLocale + ", locale = " + locale
							+ ", previousMcc = " + previousMcc + ", mcc = "
							+ mcc + ", previousMnc = " + previousMnc
							+ ", mnc = " + mnc + ", localeChanged = "
							+ localeChanged + ", this = " + this);
		}

		if (localeChanged) {
			sLocaleConfiguration.locale = locale;
			sLocaleConfiguration.mcc = mcc;
			sLocaleConfiguration.mnc = mnc;

			mIconCache.flush();

			final LocaleConfiguration localeConfiguration = sLocaleConfiguration;
			new Thread("WriteLocaleConfiguration") {
				@Override
				public void run() {
					writeConfiguration(Launcher.this, localeConfiguration);
				}
			}.start();
		}
	}

	private static class LocaleConfiguration {
		public String locale;
		public int mcc = -1;
		public int mnc = -1;
	}

	private static void readConfiguration(Context context,
			LocaleConfiguration configuration) {
		DataInputStream in = null;
		try {
			in = new DataInputStream(context.openFileInput(PREFERENCES));
			configuration.locale = in.readUTF();
			configuration.mcc = in.readInt();
			configuration.mnc = in.readInt();
		} catch (FileNotFoundException e) {
			// Ignore
			LauncherLog
					.d(TAG, "FileNotFoundException when read configuration.");
		} catch (IOException e) {
			// Ignore
			LauncherLog.d(TAG, "IOException when read configuration.");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Ignore
					LauncherLog.d(TAG, "IOException when close file.");
				}
			}
		}
	}

	private static void writeConfiguration(Context context,
			LocaleConfiguration configuration) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(context.openFileOutput(PREFERENCES,
					MODE_PRIVATE));
			out.writeUTF(configuration.locale);
			out.writeInt(configuration.mcc);
			out.writeInt(configuration.mnc);
			out.flush();
		} catch (FileNotFoundException e) {
			// Ignore
			LauncherLog.d(TAG,
					"FileNotFoundException when write configuration.");
		} catch (IOException e) {
			// noinspection ResultOfMethodCallIgnored
			context.getFileStreamPath(PREFERENCES).delete();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// Ignore
					LauncherLog.d(TAG, "IOException when close file.");
				}
			}
		}
	}

	public Stats getStats() {
		return mStats;
	}

	public LayoutInflater getInflater() {
		return mInflater;
	}

	public DragLayer getDragLayer() {
		return mDragLayer;
	}

	boolean isDraggingEnabled() {
		// We prevent dragging when we are loading the workspace as it is
		// possible to pick up a view
		// that is subsequently removed from the workspace in startBinding().
		return !mModel.isLoadingWorkspace();
	}

	static int getScreen() {
		synchronized (sLock) {
			return sScreen;
		}
	}

	static void setScreen(int screen) {
		synchronized (sLock) {
			sScreen = screen;
		}
	}

	/**
	 * Returns whether we should delay spring loaded mode -- for shortcuts and
	 * widgets that have a configuration step, this allows the proper animations
	 * to run after other transitions.
	 */
	private boolean completeAdd(PendingAddArguments args) {
		boolean result = false;
		switch (args.requestCode) {
		case REQUEST_PICK_APPLICATION:
			completeAddApplication(args.intent, args.container, args.screenId,
					args.cellX, args.cellY);
			break;
		case REQUEST_PICK_SHORTCUT:
			processShortcut(args.intent);
			break;
		case REQUEST_CREATE_SHORTCUT:
			completeAddShortcut(args.intent, args.container, args.screenId,
					args.cellX, args.cellY);
			result = true;
			break;
		case REQUEST_CREATE_APPWIDGET:
			int appWidgetId = args.intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
			completeAddAppWidget(appWidgetId, args.container, args.screenId,
					null, null);
			result = true;
			break;
		}
		// Before adding this resetAddInfo(), after a shortcut was added to a
		// workspace screen,
		// if you turned the screen off and then back while in All Apps,
		// Launcher would not
		// return to the workspace. Clearing mAddInfo.container here fixes this
		// issue
		resetAddInfo();
		return result;
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		// Reset the startActivity waiting flag
		mWaitingForResult = false;

		if (requestCode == REQUEST_BIND_APPWIDGET) {
			int appWidgetId = data != null ? data.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID, -1) : -1;
			if (resultCode == RESULT_CANCELED) {
				completeTwoStageWidgetDrop(RESULT_CANCELED, appWidgetId);
				// / M: [ALPS01298428] Strip empty screen if app widget binding
				// request is cancelled.
				// mWorkspace.stripEmptyScreens();
				// add by zhouerlong
			} else if (resultCode == RESULT_OK) {
				addAppWidgetImpl(appWidgetId, mPendingAddInfo, null,
						mPendingAddWidgetInfo);
			}
			return;
		} else if (requestCode == REQUEST_PICK_WALLPAPER) {
			if (resultCode == RESULT_OK && mWorkspace.isInOverviewMode()) {
				// / M: [ALPS01394977] Post to exit overview mode to avoid
				// getting landscape layout in snapToPage().
				mHandler.postDelayed(new Runnable() {
					public void run() {
						mWorkspace.exitOverviewMode(false);
					}
				}, 50);
			}
			return;
		} else if (requestCode == REQUEST_HIDE_APPS) { // / M: handle hide apps,
														// add for OP09.
			mWaitingForResult = false;
//			mAppsCustomizeContent.processAppsStateChanged();
			this.mAppsCustomizeContentSpringWidget.processAppsStateChanged();// add
																				// by
																				// zhouerlong
//			this.mAppsCustomizeContentSpringApps.processAppsStateChanged();// add
																			// by
																			// zhouerlong
			return;
		}

		boolean delayExitSpringLoadedMode = false;
		boolean isWidgetDrop = (requestCode == REQUEST_PICK_APPWIDGET || requestCode == REQUEST_CREATE_APPWIDGET);

		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onActivityResult: requestCode = " + requestCode
					+ ", resultCode = " + resultCode + ", data = " + data
					+ ", mPendingAddInfo = " + mPendingAddInfo);
		}

		// We have special handling for widgets
		if (isWidgetDrop) {
			int appWidgetId = data != null ? data.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID, -1) : -1;
			if (appWidgetId < 0) {
				Log.e(TAG,
						"Error: appWidgetId (EXTRA_APPWIDGET_ID) was not returned from the \\"
								+ "widget configuration activity.");
				completeTwoStageWidgetDrop(RESULT_CANCELED, appWidgetId);
				mWorkspace.stripEmptyScreens();
			} else {
				completeTwoStageWidgetDrop(resultCode, appWidgetId);
			}
			return;
		}

		// The pattern used here is that a user PICKs a specific application,
		// which, depending on the target, might need to CREATE the actual
		// target.

		// For example, the user would PICK_SHORTCUT for "Music playlist", and
		// we
		// launch over to the Music app to actually CREATE_SHORTCUT.
		if (resultCode == RESULT_OK
				&& mPendingAddInfo.container != ItemInfo.NO_ID) {
			final PendingAddArguments args = new PendingAddArguments();
			args.requestCode = requestCode;
			args.intent = data;
			args.container = mPendingAddInfo.container;
			args.screenId = mPendingAddInfo.screenId;
			args.cellX = mPendingAddInfo.cellX;
			args.cellY = mPendingAddInfo.cellY;
			if (isWorkspaceLocked()) {
				sPendingAddList.add(args);
			} else {
				delayExitSpringLoadedMode = completeAdd(args);
			}
		} else if (resultCode == RESULT_CANCELED) {
			mWorkspace.stripEmptyScreens();
		}else  {
			super.onActivityResult(requestCode, resultCode, data);		}
//		mDragLayer.clearAnimatedView();
		// Exit spring loaded mode if necessary after cancelling the
		// configuration of a widget
		exitSpringLoadedDragModeDelayed((resultCode != RESULT_CANCELED),
				delayExitSpringLoadedMode, null);
	}

	private void completeTwoStageWidgetDrop(final int resultCode,
			final int appWidgetId) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "completeTwoStageWidgetDrop resultCode = "
					+ resultCode + ", appWidgetId = " + appWidgetId
					+ ", mPendingAddInfo.screenId = "
					+ mPendingAddInfo.screenId);
		}

		CellLayout cellLayout = (CellLayout) mWorkspace
				.getScreenWithId(mPendingAddInfo.screenId);
		Runnable onCompleteRunnable = null;
		int animationType = 0;

		AppWidgetHostView boundWidget = null;
		if (resultCode == RESULT_OK) {
			animationType = Workspace.COMPLETE_TWO_STAGE_WIDGET_DROP_ANIMATION;
			final AppWidgetHostView layout = mAppWidgetHost.createView(this,
					appWidgetId, mPendingAddWidgetInfo);
			boundWidget = layout;
			onCompleteRunnable = new Runnable() {
				@Override
				public void run() {
					completeAddAppWidget(appWidgetId,
							mPendingAddInfo.container,
							mPendingAddInfo.screenId, layout, null);
					exitSpringLoadedDragModeDelayed(
							(resultCode != RESULT_CANCELED), false, null);
					mWorkspace.setAddAppWidgetSuccessfulDrop();
				}
			};
		} else if (resultCode == RESULT_CANCELED) {
			animationType = Workspace.CANCEL_TWO_STAGE_WIDGET_DROP_ANIMATION;
			onCompleteRunnable = new Runnable() {
				@Override
				public void run() {
					exitSpringLoadedDragModeDelayed(
							(resultCode != RESULT_CANCELED), false, null);
				}
			};
		}
		if (mDragLayer.getAnimatedView() != null) {
			mWorkspace.animateWidgetDrop(mPendingAddInfo, cellLayout,
					(DragView) mDragLayer.getAnimatedView(),
					onCompleteRunnable, animationType, boundWidget, true,false);
		} else {
			// The animated view may be null in the case of a rotation during
			// widget configuration
			onCompleteRunnable.run();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "(Launcher)onStop: this = " + this);
		}

		FirstFrameAnimatorHelper.setIsVisible(false);

		// / M: Exit edit mode when leave launcher, for op09.
		if (isInEditMode()) {
			exitEditMode();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "(Launcher)onStart: this = " + this);
		}

		FirstFrameAnimatorHelper.setIsVisible(true);
	}
	
	public void OnDragEnd() {
		
	}

	@Override
	protected void onResume() {
		long startTime = 0;
		if (DEBUG_RESUME_TIME) {
			startTime = System.currentTimeMillis();
			Log.v(TAG, "Launcher.onResume()");
		}
		super.onResume();
		mSensorManager.registerListener(this,  
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  
                SensorManager.SENSOR_DELAY_NORMAL);
	
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "(Launcher)onResume: mRestoring = " + mRestoring
					+ ", mOnResumeNeedsLoad = " + mOnResumeNeedsLoad
					+ ",mOrientationChanged = " + mOrientationChanged
					+ ",mPagesAreRecreated = " + mPagesWereRecreated
					+ ", this = " + this);
		}
		// / M: Call the appropriate callback for the IMtkWidget on the current
		// page when we resume Launcher.
		mWorkspace.onResumeWhenShown(mWorkspace.getCurrentPage());

		if (mWorkspace != null && mWorkspace.getPageIndicator() != null) {
			if (!mWorkspace.isInSpringLoadMoed())
			mWorkspace.getPageIndicator().setTranslationY(0f);//?????????
		}
		
		mWalListView.updateCurrentThemeFromDb();
		// Restore the previous launcher state
		if (mOnResumeState == State.WORKSPACE) {
//			showWorkspace(true);
		} else if (mOnResumeState == State.APPS_CUSTOMIZE) {
			// / M: Show ContentType related page, not always show App page.
//			showAllApps(
//					false,
//					mAppsCustomizeContent != null ? mAppsCustomizeContent
//							.getContentType()
//							: AppsCustomizePagedView.ContentType.Applications,
//					false);
		}

		// add by zhouerlong
	/*	this.mAppsCustomizeContentSpringApps
				.setContentType(AppsCustomizePagedView.ContentType.Applications);*/
		// add by zhouerlong
		mOnResumeState = State.NONE;

		mModel.OnFolderItemsChange();// A by zhouerlong
		// Background was set to gradient in onPause(), restore to black if in
		// all apps.
		setWorkspaceBackground(mState == State.WORKSPACE);

		mPaused = false;
		sPausedFromUserAction = false;
		if (mRestoring || mOnResumeNeedsLoad) {
			mWorkspaceLoading = true;
			mModel.startLoader(true, -1);
			mRestoring = false;
			mOnResumeNeedsLoad = false;
		}
		if (mBindOnResumeCallbacks.size() > 0) {
			// We might have postponed some bind calls until onResume (see
			// waitUntilResume) --
			// execute them here
			long startTimeCallbacks = 0;
			if (DEBUG_RESUME_TIME) {
				startTimeCallbacks = System.currentTimeMillis();
			}

//			if (mAppsCustomizeContent != null) {
//				mAppsCustomizeContent.setBulkBind(true); // 大批量刷新数据
//			}// add by zhouerlong
			if (this.mAppsCustomizeContentSpringWidget != null) {
				this.mAppsCustomizeContentSpringWidget.setBulkBind(true);
			}
		/*	if (this.mAppsCustomizeContentSpringApps != null) {
				this.mAppsCustomizeContentSpringApps.setBulkBind(true);
			}// add by zhouerlong
*/			for (int i = 0; i < mBindOnResumeCallbacks.size(); i++) {
				mBindOnResumeCallbacks.get(i).run();
			}
//			if (mAppsCustomizeContent != null) {
//				mAppsCustomizeContent.setBulkBind(false);
//			}// add by zhouerlong
			if (this.mAppsCustomizeContentSpringWidget != null) {
				this.mAppsCustomizeContentSpringWidget.setBulkBind(false);
			}
			/*if (this.mAppsCustomizeContentSpringApps != null) {
				this.mAppsCustomizeContentSpringApps.setBulkBind(false);
			}// add by zhouerlong
*/			mBindOnResumeCallbacks.clear();
			if (DEBUG_RESUME_TIME) {
				Log.d(TAG, "Time spent processing callbacks in onResume: "
						+ (System.currentTimeMillis() - startTimeCallbacks));
			}
		}
		if (mOnResumeCallbacks.size() > 0) {
			for (int i = 0; i < mOnResumeCallbacks.size(); i++) {
				mOnResumeCallbacks.get(i).run();
			}
			mOnResumeCallbacks.clear();
		}

		// Reset the pressed state of icons that were locked in the press state
		// while activities
		// were launching
		if (mWaitingForResume != null) {
			// Resets the previous workspace icon press state
			mWaitingForResume.setStayPressed(false);
		}
//		if (mAppsCustomizeContent != null) {
//			// Resets the previous all apps icon press state
//			mAppsCustomizeContent.resetDrawableState();
//		}
		// add by zhouerlong
		/*if (this.mAppsCustomizeContentSpringApps != null) {
			// Resets the previous all apps icon press state
			mAppsCustomizeContentSpringApps.resetDrawableState();
		}*/
		if (this.mAppsCustomizeContentSpringWidget != null) {
			// Resets the previous all apps icon press state
			mAppsCustomizeContentSpringWidget.resetDrawableState();
		}
		// add by zhouerlong
		// It is possible that widgets can receive updates while launcher is not
		// in the foreground.
		// Consequently, the widgets will be inflated in the orientation of the
		// foreground activity
		// (framework issue). On resuming, we ensure that any widgets are
		// inflated for the current
		// orientation.
		getWorkspace().reinflateWidgetsIfNecessary();

		// Process any items that were added while Launcher was away.
		InstallShortcutReceiver.disableAndFlushInstallQueue(this);

		// Update the voice search button proxy
		updateVoiceButtonProxyVisible(false);

		// Again, as with the above scenario, it's possible that one or more of
		// the global icons
		// were updated in the wrong orientation.
		updateGlobalIcons();
		if (DEBUG_RESUME_TIME) {
			Log.d(TAG, "Time spent in onResume: "
					+ (System.currentTimeMillis() - startTime));
		}

		if (mWorkspace.getCustomContentCallbacks() != null) {
			// If we are resuming and the custom content is the current page, we
			// call onShow().
			// It is also poassible that onShow will instead be called slightly
			// after first layout
			// if PagedView#setRestorePage was set to the custom content page in
			// onCreate().
			if (mWorkspace.isOnOrMovingToCustomContent()) {
				mWorkspace.getCustomContentCallbacks().onShow();
			}
		}
		mWorkspace.updateInteractionForState();
		mWorkspace.onResume();

		//added by yhf for statusbar background
		Intent intent = new Intent("com.android.systemui.statusbar.phone.RELOADBG");
		
					
		intent.putExtra("type","transparent");
								
		sendBroadcast(intent);

		//end yhf for statusbar background
	}

	@Override
	protected void onPause() {
		// Ensure that items added to Launcher are queued until Launcher returns
		InstallShortcutReceiver.enableInstallQueue();

		super.onPause();
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "(Launcher)onPause: this = " + this);
		}
		mSensorManager.unregisterListener(this); 
		// / M: Call the appropriate callback for the IMtkWidget on the current
		// page when we pause Launcher.
		mWorkspace.onPauseWhenShown(mWorkspace.getCurrentPage());

		mPaused = true;
		mDragController.cancelDrag();
		mDragController.resetLastGestureUpTime();
		mVisibleView.closeVisibleView();
		mHideView.closeHideView();
		
//		mSearchDropTargetBar.OnPause();

		// We call onHide() aggressively. The custom content callbacks should be
		// able to
		// debounce excess onHide calls.
		if (mWorkspace.getCustomContentCallbacks() != null) {
			mWorkspace.getCustomContentCallbacks().onHide();
		}
	}
	  ProgressDialog mDialog =null;
    /**
     * 切换主题进度条
     */
    public void doChangeTheme(){
	    mIconCache.flush();
	    mModel.forceReload();
	    
	    final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(this.getString(R.string.dialog_loading));
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
        
        mHandler.postDelayed(new Runnable() {
        	public void run() {
        		dialog.dismiss();
				mWorkspace.exitSpringLoadMode(true,true);
        	}
        }, 4000);
    }
    

	//add by zhouerlong 20150808 begin    
    /**
     * 触发广播时间
     */
    public void doNiftyObserable(final boolean state) {
    	mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
//				mWorkspace.OnSpringLoadFinish();
//				mNifObserables.notifyChanged(state);
				
			}
		}, 600);

//		mWorkspace.OnSpringLoadFinish();
//		mNifObserables.notifyChanged(state);
//		mNifObserables.notifyChanged();
    }

	protected void onFinishBindingItems() {
				//add by zhouerlong end 0728
		if (mWorkspace != null && hasCustomContentToLeft()
				&& mWorkspace.hasCustomContent()) {
			addCustomContentToLeft();
		}
		setupNavigration(true);
	}

	QSBScroller mQsbScroller = new QSBScroller() {
		int scrollY = 0;
		@Override
		public void setScrollY(int scroll) {
			scrollY = scroll;
			if (mWorkspace.isOnOrMovingToCustomContent()) {
				mSearchDropTargetBar.setTranslationY(-scrollY);
				getQsbBar().setTranslationY(-scrollY);
			}
		}
	};
	/**
	 * 搜素背景可以实现毛玻璃
	 */
	private AnimatorSet anim;
	//add by zhouerlong end 20150814

	public void resetQSBScroll() {
		mSearchDropTargetBar.animate().translationY(0).start();
		getQsbBar().animate().translationY(0).start();
	}
	
	//add by zhouerlong begin 20150812
	/**
	 * 注册主题广播
	 */
	public void registerThemeReceiver() {
	//add by zhouerlong begin 20150814

		IntentFilter filter = new IntentFilter();
		filter = new IntentFilter();
		filter.addAction(ThemeListView.BROADCAST_INTENTFILTER);
		filter.addAction(ThemeListView.BROADCAST_INTENTFILTER_DELETE);
		this.registerReceiver(themeReceiver, filter);
		

        IntentFilter lq_filter = new IntentFilter(OLThemeNotification.RECEIVER_ACTION);
        registerReceiver(mlqChangeIntentReceiver, lq_filter);
	}
	
	lqThemeChangeIntentReceiver mlqChangeIntentReceiver= new lqThemeChangeIntentReceiver();
	
	/**
     * Receives notifications for lqtheme
     */
    private class lqThemeChangeIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if(intent.getAction().equals(OLThemeNotification.RECEIVER_ACTION)){
        		String themePath = intent.getStringExtra(OLThemeNotification.RECEIVER_THEME_PATH);
        		String themeJarPath = "";
        		boolean notifyResult =false;
        		if (LqShredPreferences.isLqtheme(Launcher.this)) {
        		if(!themePath.equals("")){
        			if(themePath.endsWith(".jar")){
        				notifyResult =LqService.getInstance().notifyLqThemeChanged(themePath);
        				LqShredPreferences.setLqtheme(true, themePath);
        			}else{
        				LqtThemeParserAdapter parser = new LqtThemeParserAdapter();
        				themeJarPath = parser.getApplyThemeFilePath(context, themePath);
        				notifyResult =LqService.getInstance().notifyLqThemeChanged(themeJarPath);
        				LqShredPreferences.setLqtheme(true, themeJarPath);
        			}
        			LqService.getInstance().applyWallpaper(true);
        			if(mThemeListView !=null) {

            			mThemeListView.updateCurrentThemeFromDb();
        			}
        		}
        		
        		if(notifyResult){
        			OLThemeNotification.notifyThemeChange();
            		}
        		}
        		
        	}
        	
        }
    }
	
	/**
	 * 卸载广播
	 */
	public void unregisterThemeReceiver() {
	//add by zhouerlong begin 20150814
		this.unregisterReceiver(themeReceiver);
		this.unregisterReceiver(mlqChangeIntentReceiver);
	//add by zhouerlong end 20150814
	}

	

	//add by zhouerlong end 20150814
	public interface CustomContentCallbacks {
		// Custom content is completely shown
		public void onShow();

		// Custom content is completely hidden
		public void onHide();

		// Custom content scroll progress changed. From 0 (not showing) to 1
		// (fully showing).
		public void onScrollProgressChanged(float progress);
	}

	protected void startSettings() {
		// / M: [ALPS01233906] Start settings activity when clicking "SETTINGS"
		// button in overview mode.
		Intent settings = new Intent(android.provider.Settings.ACTION_SETTINGS);
		settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivitySafely(null, settings, "startSettings");
	}

	public interface QSBScroller {
		public void setScrollY(int scrollY);
	}

	public QSBScroller addToCustomContentPage(View customContent,
			CustomContentCallbacks callbacks, String description) {
		mWorkspace
				.addToCustomContentPage(customContent, callbacks, description);
		return mQsbScroller;
	}

	// The custom content needs to offset its content to account for the QSB
	public int getTopOffsetForCustomContent() {
		return mWorkspace.getPaddingTop();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG,
					"onRetainNonConfigurationInstance: mSavedState = "
							+ mSavedState + ", mSavedInstanceState = "
							+ mSavedInstanceState);
		}

		// Flag the loader to stop early before switching
		mModel.stopLoader();
//		if (mAppsCustomizeContent != null) {
//			mAppsCustomizeContent.surrender();
//		}
		// add by zhouerlong
		if (this.mAppsCustomizeContentSpringWidget != null) {
			this.mAppsCustomizeContentSpringWidget.surrender();
		}
		/*if (this.mAppsCustomizeContentSpringApps != null) {
			this.mAppsCustomizeContentSpringApps.surrender();
		}*/
		// add by zhouerlong
		return Boolean.TRUE;
	}

	// We can't hide the IME if it was forced open. So don't bother
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mHasFocus = hasFocus;
	}

	private boolean acceptFilter() {
		final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		return !inputManager.isFullscreenMode();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//add by caosw
		if (!mBindingWorkspaceFinished){
			return true;
		}
		final int uniChar = event.getUnicodeChar();
		final boolean handled = super.onKeyDown(keyCode, event);
		final boolean isKeyNotWhitespace = uniChar > 0
				&& !Character.isWhitespace(uniChar);
		if (LauncherLog.DEBUG_KEY) {
			LauncherLog.d(TAG, " onKeyDown: KeyCode = " + keyCode
					+ ", KeyEvent = " + event + ", uniChar = " + uniChar
					+ ", handled = " + handled + ", isKeyNotWhitespace = "
					+ isKeyNotWhitespace);
		}

		if (!handled && acceptFilter() && isKeyNotWhitespace) {
			boolean gotKey = TextKeyListener.getInstance().onKeyDown(
					mWorkspace, mDefaultKeySsb, keyCode, event);
			if (gotKey && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0) {
				// something usable has been typed - start a search
				// the typed text will be retrieved and cleared by
				// showSearchDialog()
				// If there are multiple keystrokes before the search dialog
				// takes focus,
				// onSearchRequested() will be called for every keystroke,
				// but it is idempotent, so it's fine.
				return onSearchRequested();
			}
		}

		// Eat the long press event so the keyboard doesn't come up.
		if (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress()) {
			return true;
		}

		return handled;
	}

	private String getTypedText() {
		return mDefaultKeySsb.toString();
	}

	private void clearTypedText() {
		mDefaultKeySsb.clear();
		mDefaultKeySsb.clearSpans();
		Selection.setSelection(mDefaultKeySsb, 0);
	}

	/**
	 * Given the integer (ordinal) value of a State enum instance, convert it to
	 * a variable of type State
	 */
	private static State intToState(int stateOrdinal) {
		State state = State.WORKSPACE;
		final State[] stateValues = State.values();
		for (int i = 0; i < stateValues.length; i++) {
			if (stateValues[i].ordinal() == stateOrdinal) {
				state = stateValues[i];
				break;
			}
		}
		return state;
	}

	/**
	 * Restores the previous state, if it exists.
	 * 
	 * @param savedState
	 *            The previous state.
	 */
	private void restoreState(Bundle savedState) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "restoreState: savedState = " + savedState);
		}

		if (savedState == null) {
			return;
		}

		State state = intToState(savedState.getInt(RUNTIME_STATE,
				State.WORKSPACE.ordinal()));
		if (state == State.APPS_CUSTOMIZE) {
			mOnResumeState = State.APPS_CUSTOMIZE;
		}

		int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN,
				PagedView.INVALID_RESTORE_PAGE);
		if (currentScreen != PagedView.INVALID_RESTORE_PAGE) {
			mWorkspace.setRestorePage(currentScreen);
		}

		final long pendingAddContainer = savedState.getLong(
				RUNTIME_STATE_PENDING_ADD_CONTAINER, -1);
		final long pendingAddScreen = savedState.getLong(
				RUNTIME_STATE_PENDING_ADD_SCREEN, -1);

		if (pendingAddContainer != ItemInfo.NO_ID && pendingAddScreen > -1) {
			mPendingAddInfo.container = pendingAddContainer;
			mPendingAddInfo.screenId = pendingAddScreen;
			mPendingAddInfo.cellX = savedState
					.getInt(RUNTIME_STATE_PENDING_ADD_CELL_X);
			mPendingAddInfo.cellY = savedState
					.getInt(RUNTIME_STATE_PENDING_ADD_CELL_Y);
			mPendingAddInfo.spanX = savedState
					.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_X);
			mPendingAddInfo.spanY = savedState
					.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y);
			mPendingAddWidgetInfo = savedState
					.getParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO);
			mWaitingForResult = true;
			mRestoring = true;
		}

		boolean renameFolder = savedState.getBoolean(
				RUNTIME_STATE_PENDING_FOLDER_RENAME, false);
		if (renameFolder) {
			long id = savedState
					.getLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID);
			mFolderInfo = mModel.getFolderById(this, sFolders, id);
			mRestoring = true;
		}

		// Restore the AppsCustomize tab
		/*if (mAppsCustomizeTabHost != null) {
			String curTab = savedState.getString("apps_customize_currentTab");
			if (curTab != null) {
				mAppsCustomizeTabHost
						.setContentTypeImmediate(mAppsCustomizeTabHost
								.getContentTypeForTabTag(curTab));
//				mAppsCustomizeContent.loadAssociatedPages(mAppsCustomizeContent
//						.getCurrentPage());
			}

			int currentIndex = savedState.getInt("apps_customize_currentIndex");
//			mAppsCustomizeContent.restorePageForIndex(currentIndex);
		}*/
	}

	class AlphaListener implements AnimatorListener {
		private View fromView = null;
		private View toView = null;

		@Override
		public void onAnimationStart(Animator animation) {
			// TODO Auto-generated method stub

		}

		public AlphaListener(View fromView,View toView) {
			super();
			this.fromView = fromView;
			this.toView = toView;
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			if (fromView != null) {
				fromView.setAlpha(0f);
				fromView.setVisibility(View.GONE);
			}
			if (toView != null) {
				toView.setAlpha(1f);
				
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animator animation) {
			// TODO Auto-generated method stub

		}

	}
	public boolean isEnterEdit() {
		return mIsEnterEdit==EditMode.ENTER_EDIT;
	}
	
	class LoadRunnable implements Runnable {
		boolean mode;
		public LoadRunnable(boolean mode) {
			super();
			this.mode = mode;
		}
		@Override
		public void run() {
			mNavigationLayout.enterSprindLoadMode(mode, mWorkspace,mWorkspace.getDefaultpage());
		}
		
	}
	
	
	public void setupNavigration(boolean mode) {

//		mHandler.postDelayed(new LoadRunnable(mode), 300);

		if(mNavigationLayout.getChildCount()!=0) {
			mNavigationLayout.removeAllViews();
		}
		mNavigationLayout.enterSprindLoadMode(mode, mWorkspace,mWorkspace.getDefaultpage());
	}

	/**
	 * 进入就是编辑模式的动画
	 * 
	 * @param state
	 *            NONE, WALLPAPER, WIDGET, APPS, THEMES, ANIM_EFFECT
	 * @param duration
	 * @param id
	 */
	public void setupChangeAnimation(SpringState state, int duration, int id) {
		if (!style) {
			if (state == this.mSpringState) {
				return;
			}
			onChangeTextSize(id, this.mOverViewTabsRadioGroup,
					R.id.wallpaper_button);
		}
		
		duration = 500;
		if(anim != null &&anim.isRunning()) {
			anim.cancel();
			anim  = null;
		}
		 anim = new AnimatorSet();
		anim.setDuration(duration);
		final float toAlpha = 1f;
		final float fromAlpha = 0.0f;

		final SpringState oldState = this.mSpringState;
		this.mSpringState = state;
		boolean isBatchModel =false;//= state == SpringState.BATCH_EDIT_APPS ||(oldState == SpringState.BATCH_EDIT_APPS) ;
		toView = mWalListView;
		toView.setVisibility(View.VISIBLE);
		View fromView = mHideAppsView;
		if(style) {
			fromView=mOverViewTabsRadioGroup;
		}
		View togleView = mOverViewTabsRadioGroup;
		ViewGroup rootView = (ViewGroup) mOverViewTabsRadioGroup.getParent();
		int rootHeight = rootView.getHeight();
		
		View backtogleView = null;
		if (isBatchModel) {
			this.doNiftyObserable(true);
		}

        Log.i("zhouerlong", "耗时111111:"+(SystemClock.uptimeMillis()-Launcher.time));
		
		if (style || isBatchModel) {
			if (style&&id == FIRST_ENTER_OVER_SPRING_ID) {
				mNavigationLayout.setAlpha(0f);
				id = BACK_OVER_SPRING_ID;
			}
			if (id == 0) {
//				this.doNiftyObserable();
				mIsEnterEdit = EditMode.NORMAL_EDIT;
			} else {
				mIsEnterEdit = EditMode.ENTER_EDIT;
			}
		}
		if (style||isBatchModel) {
			if (state == SpringState.NONE) {
				toView = mOverViewTabsRadioGroup;
			}
		}
		for (SpringState key : this.mAllSpringViews.keySet()) {

			if (key != state && key != oldState) {
				View targetView = this.mAllSpringViews.get(key);
				if (targetView != null) {
					targetView.setAlpha(0f);
					targetView.setVisibility(View.GONE);
				}
			}

			if (key == oldState) {
				fromView = mAllSpringViews.get(key);
				if (fromView.getAlpha() != 0f) {
					fromView.setAlpha(0f);
					fromView.setVisibility(View.GONE);
				}

			}
			
			if (key == state) {
				toView = mAllSpringViews.get(key);

				toView.setAlpha(0f);
				toView.setVisibility(View.VISIBLE);
				toView.bringToFront();
			}
		}
		
		
		if (style||isBatchModel) {
			if (state == SpringState.NONE) {
				toView= mOverViewTabsRadioGroup;
				if (style) {
					toView.bringToFront();
					toView.setVisibility(View.VISIBLE);
				}
				backtogleView  = mAlignmentLinearLayout;
			}
		}
		

		ObjectAnimator toViewAlpha = ObjectAnimator.ofFloat(toView, "alpha",
				fromAlpha, toAlpha);
		toViewAlpha.setInterpolator(new ZoomInInterpolator());

		ObjectAnimator toTransation = ObjectAnimator.ofFloat(toView,
				"translationY", rootHeight-toView.getTop(), toView.getTop());
		toTransation.setInterpolator(mZoomInInterpolator);


		// 这是启动from视图动画
		// M by zhouerlong

		ObjectAnimator fromViewAlpha = ObjectAnimator.ofFloat(fromView,
				"alpha", toAlpha, fromAlpha);

		fromViewAlpha.setInterpolator(mZoomInInterpolator);
		ObjectAnimator fromTransation = ObjectAnimator.ofFloat(fromView,
				"translationY", fromView.getTop(), rootHeight-fromView.getTop());

		/*
		 * 这个是radiogroupView的透明度动画
		 */
		ObjectAnimator togleViewAlpha = ObjectAnimator.ofFloat(togleView,
				"alpha", toAlpha, fromAlpha);

		togleViewAlpha.setInterpolator(mZoomInInterpolator);
		
		/*
		 * 这个是radiogroupView的位移动画
		 */
		ObjectAnimator togleTransation = ObjectAnimator.ofFloat(togleView,
				"translationY", togleView.getTop(), rootHeight-togleView.getTop());
		togleTransation.setInterpolator(mZoomInInterpolator);
		
		/*
		 * 返回时候多出的一个View 这是在进入批量编辑才特有的动画
		 */
		
		ObjectAnimator backtogleViewAlpha = ObjectAnimator.ofFloat(backtogleView,
				"alpha",fromAlpha , toAlpha);
		backtogleViewAlpha.setInterpolator(mZoomInInterpolator);
 
		ObjectAnimator backtogleTransation = null;
		if (backtogleView != null) {
			backtogleTransation  = ObjectAnimator.ofFloat(backtogleView,
					"translationY",rootHeight-backtogleView.getTop(), backtogleView.getTop() );
			backtogleTransation.setInterpolator(mZoomInInterpolator);
		}
		if (style || isBatchModel) {
			if (id == BACK_OVER_SPRING_ID) {
				anim.play(fromViewAlpha).with(fromTransation);
				anim.play(fromTransation).with(toTransation);
				anim.play(toTransation).with(toViewAlpha);
				if (backtogleView != null) {
					anim.play(fromViewAlpha).with(backtogleViewAlpha);
					anim.play(backtogleViewAlpha).with(backtogleTransation);
				}
			} else if (id == FIRST_ENTER_OVER_SPRING_ID) {

			} else {

				anim.play(fromViewAlpha).with(fromTransation);
				anim.play(fromTransation).with(togleViewAlpha);
				anim.play(togleViewAlpha).with(togleTransation);

				anim.play(toTransation).with(togleTransation);
				anim.play(toTransation).with(toViewAlpha);

			}
		} else {
//			anim.play(fromViewAlpha).with(toViewAlpha);
			anim.play(toViewAlpha);
		}

		anim.addListener(new AlphaListener(fromView, togleView));

		anim.start();
	}

	public void setupBatchEditAppsAniamtion() {

	}

	/**
	 * 特效和进入主菜单动画切换方法
	 * 
	 * @param state
	 *            当前有两种
	 *             { EFFECTS,ANIMS,NONE }
	 * @param delay
	 * @param id
	 *//*
		public void setupChangeEffects(EffectState state, int delay, int id) {

			onChangeTextSize(id,mEffectAndAppsAnimationRadioGroup,R.id.effects_workspace);
			if (state == this.mEffectState) {
				return;
			}
			AnimatorSet anim = new AnimatorSet();
			final float toAlpha = 1f;
			final float fromAlpha = 0f;
			// anim.setDuration(delay);
			// add by zhouerlong

			final EffectState oldState = this.mEffectState;

			// add by zhouerlong
			this.mEffectState = state;
			View toView =this.findViewById(R.id.effect);
			View fromView = this.findViewById(R.id.animation);

			for (EffectState key : this.mEffectsViews.keySet()) {
				if (key == state) {
					toView = mEffectsViews.get(key);

					toView.setAlpha(0f);

//					toView.bringToFront();
					toView.getParent().bringChildToFront(toView);
					toView.setVisibility(View.VISIBLE);
				}
				if (key == oldState) {
					fromView = mEffectsViews.get(key);
					if (fromView.getAlpha() != 0f) {
						fromView.setAlpha(0f);
						fromView.setVisibility(View.GONE);
					}

				}
				if (key != state && key != oldState) {
					View targetView = this.mEffectsViews.get(key);
					if (targetView != null) {
						targetView.setAlpha(0f);
					}
				}
			}
			ObjectAnimator toViewAlpha = ObjectAnimator.ofFloat(toView, "alpha",
					fromAlpha, toAlpha);
			toViewAlpha.setDuration(delay);
			toViewAlpha.setInterpolator(mZoomInInterpolator);

			ObjectAnimator toTransation = ObjectAnimator.ofFloat(toView,
					"translationY", toView.getHeight(), toView.getTop());

			toTransation.setDuration(delay);

			ObjectAnimator fromViewAlpha = ObjectAnimator.ofFloat(fromView,
					"alpha", toAlpha, fromAlpha);

			fromViewAlpha.setDuration(delay / 4);
			fromViewAlpha.setInterpolator(mZoomInInterpolator);
			ObjectAnimator fromTransation = ObjectAnimator.ofFloat(fromView,
					"translationY", fromView.getTop(), fromView.getHeight());
			fromTransation.setDuration(delay);

			anim.play(fromViewAlpha).with(toViewAlpha);
			anim.addListener(new AlphaListener(fromView,toView));

			anim.start();
		}*/

	/**
	 * Finds all the views we need and configure them properly.
	 */

	// A by zel
	public SpringState getCurrrentSpringState() {
		int id = this.mOverViewTabsRadioGroup.getCheckedRadioButtonId();
		switch (id) {
		case R.id.wallpaper_button:
			return SpringState.WALLPAPER;
		case R.id.widget_button:
			return SpringState.WIDGET;
		case R.id.apps_edit_button:
			return SpringState.WALLPAPER;
		case R.id.theme_button:
			return SpringState.THEMES;//add by zel
		case R.id.anim_button:
			return SpringState.ANIM_EFFECT;
		default:
			return SpringState.WALLPAPER;
		}
	}

	/**
	 * 上对齐
	 */
	public void alignmentUpForCurrentCellLayout(Runnable r) {
		this.getworkspace().alignmentUpForCurrentCellLayout(r);
	}

	/**
	 * 下对齐
	 */
	public void alignmentDownForCurrentCellLayout(Runnable r) {
		this.getworkspace().alignmentDownForCurrentCellLayout(r);
	}

//add by zhouerlong begin 0728
	/**通过APP主类名实现查找View
	 * @param component 类组件
	 * @return
	 */
	public View getIconViewForComponentName(ComponentName component) {
		return mWorkspace.getViewForComponentName(component);

	}
	public ComponentName getCalendarComponentName() {
		return mCalendarComponentName;
	}
	
	/* (non-Javadoc)
	 * 改变日历图片
	 * @see com.android.launcher3.LauncherModel.Callbacks#dochangeDataIcon(int, int, java.lang.String)
	 */
	public void dochangeDataIcon(int date, int week, String bg) {/*
		BubbleTextView v = (BubbleTextView) getIconViewForComponentName(getCalendarComponentName());
		if (v != null) {
			int decade = date / 10;
			int unit = date % 10;
			Bitmap decadeIcon = ThemeInfoUtils.getCalendarIcon(
					String.valueOf(decade), this);
			Bitmap unitIcon = ThemeInfoUtils.getCalendarIcon(
					String.valueOf(unit), this);
			Bitmap calendarBg = ThemeInfoUtils.getCalendarIcon(
					String.valueOf(bg), this);
			Bitmap calendarWeek = ThemeInfoUtils.getCalendarIcon("weekday_"+String.valueOf(week), this);
			Bitmap dest = ImageUtils
					.doodlesrc(decadeIcon, unitIcon, calendarBg,calendarWeek);
			if (dest != null) {
				v.setCompoundDrawables(null,
						Utilities.createIconDrawable(dest), null, null);
				v.requestLayout();
			}
		}
	*/}

	// A by zel
	private void onChangeTextSize(int id,RadioGroup rg,int defult) {

		if (id == 0) {
			id = defult;
		}
		for (int i = 0; i < rg.getChildCount(); i++) {
			View v = rg.getChildAt(i);
			if (v instanceof RadioButton) {
				RadioButton r = (RadioButton) v;
				if (id == rg.getChildAt(i).getId()) {
					r.setTextSize(this.getResources().getDimension(
							R.dimen.text_focused_size));
					 r.setTextColor(this.getResources().getColor(R.color.radio_color_focus));
				} else {
					r.setTextSize(this.getResources().getDimension(
							R.dimen.text_normal_size));
					 r.setTextColor(this.getResources().getColor(R.color.radio_color_normal));
					// A by zel

				}
			}
		}
	}
	
	
	
	/**启动搜素模式动画
	 * @param state
	 * @return
	 */
	public Animator setupSearchViewAnimation(FLING_STATE state,boolean notSearched) {
		float fingstate = (state == FLING_STATE.UP) ? 0f : 1f;
		float searchstate = (state == FLING_STATE.UP) ? 1f : 0f;

		AnimatorSet anim = new AnimatorSet();

		final boolean up = (state == FLING_STATE.UP) ? true : false;
		final int duration = 800;
		final View overviewPanel = getOverviewPanel();
		final View hotseat = getHotseat();
		final SearchView searchView = getSearchView();
		final LauncherBackgroudView launcherBg  = mWallpaperBg;
		final NumberView numberView = (NumberView) searchView.findViewById(R.id.key_gard);
		final ViewPager pageView = (ViewPager) searchView.findViewById(R.id.myviewpager);

		ObjectAnimator hotseatAlpha = ObjectAnimator.ofFloat(hotseat, "alpha",
				fingstate);
		ObjectAnimator overviewPanelAlpha = ObjectAnimator.ofFloat(
				overviewPanel, "alpha", fingstate);

		ObjectAnimator workspaceAlpha = ObjectAnimator.ofFloat(getworkspace(),
				"alpha", fingstate);

		anim.setDuration(duration);

		float from = up ? searchView.getHeight() : 0;
		float to = up ? 0 : searchView.getHeight();
		float pageFrom = up ? -pageView.getHeight():0;
		float pageTo = up ? 0:-pageView.getHeight();
		ObjectAnimator keyGardTranslationY = ObjectAnimator.ofFloat(numberView,
				"translationY", from, to);
		ObjectAnimator pageViewTranslationY = ObjectAnimator.ofFloat(pageView,
				"translationY", pageFrom, pageTo);

		ObjectAnimator searchAlpha = ObjectAnimator.ofFloat(searchView,
				"alpha", searchstate);
		ObjectAnimator launcherBgAlpha = ObjectAnimator.ofFloat(launcherBg,
				"alpha", searchstate);

		AlphaUpdateListener alphaUpdateListener = new AlphaUpdateListener(
				overviewPanel);// 监听更新状态 编辑画板
		ObjectAnimator pageIndicatorAlpha = null;
		if (getworkspace().getPageIndicator() != null) {
			pageIndicatorAlpha = ObjectAnimator.ofFloat(getworkspace()
					.getPageIndicator(), "alpha", fingstate);
			pageIndicatorAlpha.setDuration(duration);

			alphaUpdateListener = new AlphaUpdateListener(getworkspace()
					.getPageIndicator());
			pageIndicatorAlpha.addListener(alphaUpdateListener);
			pageIndicatorAlpha.addUpdateListener(alphaUpdateListener);

		}
		anim.setInterpolator(new ZoomInInterpolator());
		if (up) {
			anim.play(overviewPanelAlpha);
			anim.play(hotseatAlpha);
			anim.play(keyGardTranslationY);
			anim.play(searchAlpha);
			anim.play(workspaceAlpha);
			anim.play(pageIndicatorAlpha);
			anim.play(pageIndicatorAlpha).before(pageViewTranslationY);
		}else {
			if (notSearched) {

				anim.play(pageViewTranslationY).before(overviewPanelAlpha);
			}else {
				anim.play(pageViewTranslationY).before(overviewPanelAlpha);
			}
			anim.play(overviewPanelAlpha).with(hotseatAlpha)
					.with(launcherBgAlpha).with(keyGardTranslationY)
					.with(searchAlpha).with(workspaceAlpha)
					.with(pageIndicatorAlpha);
		}
		
		
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

				getworkspace().occupySearchModel();
				// add by zhouerlong
				if (!up) {

					getworkspace().setVisibility(View.VISIBLE);
				} else {

					searchView.setVisibility(View.VISIBLE);
					searchView.onOpen();
					searchView.bringToFront();
					launcherBg.setVisibility(View.VISIBLE);
				}
				// 收缩搜索模式时候 将workspace 设为可见
				getworkspace().setLayerType(View.LAYER_TYPE_HARDWARE, null);
				overviewPanel.setLayerType(View.LAYER_TYPE_HARDWARE, null);
				hotseat.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				getworkspace().releaseSearchModel();
				getworkspace().setLayerType(View.LAYER_TYPE_NONE, null);
				overviewPanel.setLayerType(View.LAYER_TYPE_NONE, null);
				hotseat.setLayerType(View.LAYER_TYPE_NONE, null);
				searchView.setVisibility(View.GONE);
				

				if (up) {

					getworkspace().setVisibility(View.GONE);
					searchView.setVisibility(View.VISIBLE);
					launcherBg.setAlpha(1f);
				}else {

					getworkspace().setVisibility(View.VISIBLE);
//					searchView.onclose();

					launcherBg.setVisibility(View.GONE);
					launcherBg.setAlpha(0f);
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});

		return anim;
	}
	
	
	/**批处理预处理
	 * @param state 是否编辑模式
	 * @param isClick 是否是主动切换
	 */
	public void onMultipleEditIcons(MutipleEditState state,boolean isClick) {
		if (state == MutipleEditState.EDIT) {
			mIconState = IconChangeState.EDIT;
			doNiftyObserable(true);
			mSpringState = SpringState.BATCH_EDIT_APPS;

			mMulEditNagiration.togle(mWorkspace.getMultipleDragViews().size());
		}else {
			/*if (isClick) {
				mIconState = IconChangeState.EDIT;
			}*/
			if(mIconState != IconChangeState.DEL) {
				mIconState = IconChangeState.DEL;
				doNiftyObserable(true);
			}else {

				doNiftyObserable(false);
			}
			mSpringState = SpringState.NONE;
		}
	}
	private void setupViews() {
		final DragController dragController = mDragController;
		
		mLauncherView = findViewById(R.id.launcher);
		mLauncherBackground = (LauncherBackgroudView) findViewById(R.id.launcher_bg);
		mWallpaperBg = (LauncherBackgroudView) findViewById(R.id.wallpaper_bg);
        mExplosionField = ExplosionField.attach2Window(this);
        
		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		mWorkspace = (Workspace) mDragLayer.findViewById(R.id.workspace);
		mPageIndicators = mDragLayer.findViewById(R.id.page_indicator);//add by xxf
		mNavigationLayout = (PrizeNavigationLayout) mDragLayer.findViewById(R.id.page_navigation);
		mNavigationLayout.setLauncher(this);
		mMulEditNagiration = (PrizeMultipleEditNagiration) findViewById(R.id.multiple);
		mMulEditNagiration.setlauncher(this);
		mLauncherView
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

		// Setup the drag layer
		mDragLayer.setup(this, dragController);
		mSearchView = (SearchView) this.findViewById(R.id.key_search);
		mSearchView.setLauncher(this);
		mSearchView.setIconCache(mIconCache);
		mExplosionDialogView = (ExplosionView) LayoutInflater.from(this).inflate(
				R.layout.explosion_view, null);
		mExplosionDialogView.setExplosionField(mExplosionField);
		
//		mSearchBgView = this.findViewById(R.id.search_bg);
		//mOtherView = this.findViewById(R.id.launcher_bg);//prize-launcher_bg-xiaxuefeng-2015-7-31
		
		
		// Setup the hotseat
		mHotseat = (Hotseat) findViewById(R.id.hotseat);
		if (mHotseat != null) {
			mHotseat.setup(this);
			mHotseat.setOnLongClickListener(this);
		}

		mFolderIconImageView = new ImageView(this);// add by zhouerlong
													// ?????????????
		

		// / M: for op09 add editmode and hide app click listener.
		if (mSupportEditAndHideApps) {
			View editAppsButton = findViewById(R.id.edit_app_button);
			editAppsButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LauncherLog.d(TAG, "onClick:  v = " + v);
					enterEditMode();
					showAllApps(false,
							AppsCustomizePagedView.ContentType.Applications,
							true);
				}
			});
			editAppsButton.setOnTouchListener(getHapticFeedbackTouchListener());
			editAppsButton.setVisibility(View.VISIBLE);

			View hideAppsButton = findViewById(R.id.hide_app_button);
			hideAppsButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					startHideAppsActivity();
				}
			});
			hideAppsButton.setOnTouchListener(getHapticFeedbackTouchListener());
			hideAppsButton.setVisibility(View.VISIBLE);// m by zhouerlong
			/*
			 * View editSpacer = findViewById(R.id.edit_spacer); View hideSpacer
			 * = findViewById(R.id.hide_spacer);
			 */
			/*
			 * editSpacer.setVisibility(View.VISIBLE);
			 * hideSpacer.setVisibility(View.VISIBLE);
			 */// m by zhouerlong
		}

		// Setup the workspace
		mWorkspace.setHapticFeedbackEnabled(false);
		mWorkspace.setOnLongClickListener(this);
		mWorkspace.setup(dragController);
		dragController.addDragListener(mWorkspace);

		// Get the search/delete bar
		mSearchDropTargetBar = (SearchDropTargetBar) mDragLayer
				.findViewById(R.id.qsb_bar);
		mSearchDropTargetBar.setVisibility(View.GONE);

		// Setup AppsCustomize
//		mAppsCustomizeTabHost = (AppsCustomizeTabHost) findViewById(R.id.apps_customize_pane);
//		mAppsCustomizeContent = (AppsCustomizePagedView) mAppsCustomizeTabHost
//				.findViewById(R.id.apps_customize_pane_content);
//		// add by zhouerlong
//		mAppsCustomizeContent.setup(this, dragController);
		
		setupOverModelView();

		this.mAppsCustomizeContentSpringWidget.setup(this, dragController);
//		this.mAppsCustomizeContentSpringApps.setup(this, dragController);
		dragController.setDragScoller(mWorkspace);
		dragController.setScrollView(mDragLayer);
		dragController.setMoveTarget(mWorkspace);
		dragController.addDropTarget(mWorkspace);
		if (mSearchDropTargetBar != null) {
			mSearchDropTargetBar.setup(this, dragController);
		}
		
		

		if (getResources().getBoolean(R.bool.debug_memory_enabled)) {
			Log.v(TAG, "adding WeightWatcher");
			mWeightWatcher = new WeightWatcher(this);
			mWeightWatcher.setAlpha(0.5f);
			((FrameLayout) mLauncherView).addView(mWeightWatcher,
					new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.MATCH_PARENT,
							FrameLayout.LayoutParams.WRAP_CONTENT,
							Gravity.BOTTOM));

			boolean show = shouldShowWeightWatcher();
			mWeightWatcher.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}
	
	public PrizeNavigationLayout getNavigationLayout() {
		return mNavigationLayout;
	}
	public void setNavigationLayout(PrizeNavigationLayout mNavigationLayout) {
		this.mNavigationLayout = mNavigationLayout;
	}
	public void setupOverModelView() {
		// M b zel
		mOverViewTabsRadioGroup = (RadioGroup) this
				.findViewById(R.id.main_tab_group);
		if (style) {
			mWidgetRadioButton = (RadioButton) this
					.findViewById(R.id.widget_button);
			mThemeRadioButton = (RadioButton) this
					.findViewById(R.id.theme_button);
			mWallpaperRadioButton = (RadioButton) this
					.findViewById(R.id.wallpaper_button);

			mEffectsRadioButton = (RadioButton) this.mOverViewTabsRadioGroup
					.findViewById(R.id.anim_button);
			Drawable effct = this
					.getDrawable(R.drawable.thumbnail_entry_transition_effect1);
			Drawable widget = this
					.getDrawable(R.drawable.thumbnail_entry_add_widget1);
			Drawable themes = this
					.getDrawable(R.drawable.thumbnail_entry_multi_select1);
			Drawable wallpapers = this
					.getDrawable(R.drawable.thumbnail_entry_set_wallpaper1);
			Utilities.resizeIconDrawable(effct);
			mEffectsRadioButton.setCompoundDrawables(null, effct, null, null);
			Utilities.resizeIconDrawable(widget);
			mWidgetRadioButton.setCompoundDrawables(null, widget, null, null);
			Utilities.resizeIconDrawable(themes);
			mThemeRadioButton.setCompoundDrawables(null, themes, null, null);
			Utilities.resizeIconDrawable(wallpapers);
			mWallpaperRadioButton.setCompoundDrawables(null, wallpapers, null,
					null);
		} else {

			// add by zel
			
			RadioButton rb = (RadioButton) this.mOverViewTabsRadioGroup
					.findViewById(R.id.anim_button);

			Drawable d = getDrawable(R.drawable.old_btn_bg);
			int width = d.getIntrinsicWidth() / 3;
			int height = d.getIntrinsicHeight() / 3;
			d.setBounds(0, 0, width, height);
			rb.setCompoundDrawables(null, d, null, null);
			 
		}
		ImageUtils.createHelpericon(this);
		mOverViewTabsRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					// add by zel
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						RadioButton b = (RadioButton) group
								.findViewById(checkedId);
						int stagger = getResources().getInteger(
								R.integer.config_SpringSwitchAnimationStagger);
						switch (checkedId) {
						case R.id.wallpaper_button:
							setupChangeAnimation(SpringState.WALLPAPER,
									stagger, checkedId);
							break;
						case R.id.anim_button:
							setupChangeAnimation(SpringState.ANIM_EFFECT,
									stagger, checkedId);
							break;
						case R.id.widget_button:
							setupChangeAnimation(SpringState.WIDGET, stagger,
									checkedId);
							break;
						case R.id.apps_edit_button:
							/*setupChangeAnimation(SpringState.APPS_EDIT,
									stagger, checkedId);*/
							if (mWorkspace.isInSpringLoadMoed()) {
								mWorkspace.exitSpringLoadMode(true,false);

								if (mWorkspace.isInDragModed()) {
									mWorkspace.exitDragMode(true, false);
								}else {
									mWorkspace.enterInDragMode();
								}
							}
							
							break;
						case R.id.theme_button:
							setupChangeAnimation(SpringState.THEMES, stagger,
									checkedId);

						}
						if (AppsCustomizePagedView.DISABLE_ALL_APPS) {
							b.setChecked(false);
						}

					}

				});
		// M b zel
		mOverviewPanel.setAlpha(0f);

		this.mAppsCustomizeContentSpringWidget = (AppsCustomizePagedView) this.mOverviewPanel
				.findViewById(R.id.apps_customize_pane_content_spring_widgets);

		mAppsCustomizeContentSpringWidget
				.setContentType(AppsCustomizePagedView.ContentType.Widgets);
		// add by zhouerlong
		this.mAllSpringViews.put(SpringState.WIDGET,
				this.mAppsCustomizeContentSpringWidget);
		// add by zhouerlong
		/*this.mAppsCustomizeContentSpringApps = (AppsCustomizePagedView) this.mOverviewPanel
				.findViewById(R.id.apps_customize_pane_content_spring_apps);*/
		if (!style) {
			mAlignmentLinearLayout = (AlignmentLinearLayout) this.mOverviewPanel
					.findViewById(R.id.apps_alignment);
			mAlignmentLinearLayout.setLauncher(this);

			this.mAllSpringViews.put(SpringState.APPS_EDIT,
					this.mAlignmentLinearLayout);
			
			mHideAppsView = (HideAppsView) findViewById(R.id.hide_apps);
			mHideAppsView .setup(this);
			

//			this.mAllSpringViews.put(SpringState.BATCH_EDIT_APPS,
//					this.mHideAppsView);
			
			
		}
		mHideView = HideAppView.fromXml(this);
		mVisibleView = VisibleAppsView.fromXml(this);
		mVisibleView.setHideView(mHideView);
		mHideView.setVisibleView(mVisibleView);
		
		this.mThemeListView = (ThemeListView) this.mOverviewPanel
				.findViewById(R.id.theme_list_id);
		this.mWalListView = (WallpaperListView) this.mOverviewPanel
				.findViewById(R.id.wall_list_id);
		mThemeListView.setLauncher(this);
		mWalListView.setLauncher(this);


		SharedPreferences sp = getSharedPreferences("load_default_res",
				Context.MODE_PRIVATE);
		boolean isloaded = sp.getBoolean("load_default_res_loaded", false);
		if(isloaded) {
			mWalListView.init(Launcher.this, R.layout.wallpapers_list_item);
			mThemeListView.init(Launcher.this,
					R.layout.themes_list_item);
		}
		// add by zhouerlong begin 20150812
		this.registerThemeReceiver();
		// add by zhouerlong end 20150812
		this.mThemesParent = this.mOverviewPanel.findViewById(R.id.theme_list);
		this.mWallpaperParent = this.mOverviewPanel.findViewById(R.id.wallpaper_list);
		this.mAllSpringViews.put(SpringState.THEMES, this.mThemesParent);
		this.mAllSpringViews.put(SpringState.WALLPAPER, this.mWallpaperParent);

		this.mEffectLayoutView = (EffectLinearLayout) this.mOverviewPanel
				.findViewById(R.id.effect_list);
		this.mEffectLayoutView.setLauncher(this);
		this.mEffectParent = this.mOverviewPanel.findViewById(R.id.effect);
		mAllSpringViews.put(SpringState.ANIM_EFFECT, this.mEffectParent);

	//mdf by zhouerlong begin 20150812
//		this.mThemesLinearLayoutView.updateList(mThemesLinearLayoutView.load());
//		this.mThemesLinearLayoutView.setCurrentSelect();

		this.mEffectLayoutView.setupThemesFromAdapter(this,
				R.layout.effects_list_item, 0,
				this.mEffectLayoutView.getItemInfos());
		this.mEffectLayoutView.setCurrentSelect();
	}

	public View getmSearchBgView() {
		return null;//mSearchBgView;
	}

	/**
	 * Creates a view representing a shortcut.
	 * 
	 * @param info
	 *            The data structure describing the shortcut.
	 * 
	 * @return A View inflated from R.layout.application.
	 */
	View createShortcut(ShortcutInfo info) {
		mSearchView.addapp(info);
		return createShortcut(R.layout.application,
				(ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentPage()),
				info);
	}
	/**
	 * Creates a view representing a shortcut inflated from the specified
	 * resource.
	 * 
	 * @param layoutResId
	 *            The id of the XML layout used to create the shortcut.
	 * @param parent
	 *            The group the shortcut belongs to.
	 * @param info
	 *            The data structure describing the shortcut.
	 * 
	 * @return A View inflated from layoutResId.
	 */
	View createShortcut(int layoutResId, ViewGroup parent, ShortcutInfo info) {
		BubbleTextView favorite = (BubbleTextView) mInflater.inflate(
				layoutResId, parent, false);
	//add by zhouerlong begin 20150814
//		favorite.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.icon_drawable_padding));
		
	//add by zhouerlong begin 20150814
		favorite.applyFromShortcutInfo(info, mIconCache);
		favorite.setOnClickListener(this);
	//add by zhouerlong 20150808 begin
		/*
		 * 检测是否为系统app 是的话不注册 因为系统app 卸载不了
		 */
		if ((info.flags & AppInfo.DOWNLOADED_FLAG) != 0)  {
			favorite.registerObserver(mNifObserables);
		}
	//add by zhouerlong 20150808 end
		return favorite;
	}
	
	
	public IconCache getIconCache() {
		return mIconCache;
	}
		//add by zel
	// m by zhouerlong
	public void OnChangeAnimation(View toView) {

		final int fadeDuration = this.getResources()
				.getInteger(R.integer.config_appsCustomizeFadeInTime);// 淡入
		if(mAnimationList!=null)
		mAnimationList.clear();

		AbsAppsAnimation anim = this.getChangeAppsAnimation();
		mAnimationList = anim.getAnimators(fadeDuration * 10, new DecelerateInterpolator(1.5f), toView,false);
		anim.setPreAnimationData(toView);

		AnimatorSet animatorSet = LauncherAnimUtils.createAnimatorSet();
			for(int i=0;i<mAnimationList.size();i++) {
				
				animatorSet.play(mAnimationList.get(i));
			}
			final AnimatorSet stateAnimation  = animatorSet;
			final View v = toView;
			final Runnable startAnimRunnable = new Runnable() {
				public void run() {
					// Check that mStateAnimation hasn't changed while
					// we waited for a layout/draw pass
					setPivotsForZoom(v, 0);
					LauncherAnimUtils.startAnimationAfterNextDraw(
							stateAnimation, v);
				}
			};
			startAnimRunnable.run();
	}
		//add by zel

	// m by zhouerlong
	/**
	 * Add an application shortcut to the workspace.
	 * 
	 * @param data
	 *            The intent describing the application.
	 * @param cellInfo
	 *            The position on screen where to create the shortcut.
	 */
	void completeAddApplication(Intent data, long container, long screenId,
			int cellX, int cellY) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "completeAddApplication: Intent = " + data
					+ ", container = " + container + ", screenId = " + screenId
					+ ", cellX = " + cellX + ", cellY = " + cellY);
		}

		final int[] cellXY = mTmpAddItemCellCoordinates;
		final CellLayout layout = getCellLayout(container, screenId);

		// First we check if we already know the exact location where we want to
		// add this item.
		if (cellX >= 0 && cellY >= 0) {
			cellXY[0] = cellX;
			cellXY[1] = cellY;
		} else if (!layout.findCellForSpan(cellXY, 1, 1)) {
			showOutOfSpaceMessage(isHotseatLayout(layout));
			return;
		}

		final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(),
				data, this);

		if (info != null) {
			info.setActivity(this, data.getComponent(),
					Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			info.container = ItemInfo.NO_ID;
			mWorkspace.addApplicationShortcut(info, layout, container,
					screenId, cellXY[0], cellXY[1], isWorkspaceLocked(), cellX,
					cellY);
		} else {
			Log.e(TAG, "Couldn't find ActivityInfo for selected application: "
					+ data);
		}
	}

	/**
	 * Add a shortcut to the workspace.
	 * 
	 * @param data
	 *            The intent describing the shortcut.
	 * @param cellInfo
	 *            The position on screen where to create the shortcut.
	 */
	private void completeAddShortcut(Intent data, long container,
			long screenId, int cellX, int cellY) {
		int[] cellXY = mTmpAddItemCellCoordinates;
		int[] touchXY = mPendingAddInfo.dropPos;
		CellLayout layout = getCellLayout(container, screenId);

		boolean foundCellSpan = false;

		ShortcutInfo info = mModel.infoFromShortcutIntent(this, data, null);
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "completeAddShortcut: info = " + info
					+ ", data = " + data + ", container = " + container
					+ ", screenId = " + screenId + ", cellX = " + cellX
					+ ", cellY = " + cellY);
		}

		if (info == null) {
			return;
		}
		final View view = createShortcut(info);

		// First we check if we already know the exact location where we want to
		// add this item.
		if (cellX >= 0 && cellY >= 0) {
			cellXY[0] = cellX;
			cellXY[1] = cellY;
			foundCellSpan = true;

			// If appropriate, either create a folder or add to an existing
			// folder
			if (mWorkspace.createUserFolderIfNecessary(view, container, layout,
					cellXY, 0, true, null, null,null)) {
				return;
			}
			DragObject dragObject = new DragObject();
			dragObject.dragInfo = info;
			if (mWorkspace.addToExistingFolderIfNecessary(view, layout, cellXY,
					0, dragObject, true)) {
				return;
			}
		} else if (touchXY != null) {
			// when dragging and dropping, just find the closest free spot
			int[] result = layout.findNearestVacantArea(touchXY[0], touchXY[1],
					1, 1, cellXY);
			foundCellSpan = (result != null);
		} else {
			foundCellSpan = layout.findCellForSpan(cellXY, 1, 1);
		}

		if (!foundCellSpan) {
			showOutOfSpaceMessage(isHotseatLayout(layout));
			return;
		}

		LauncherModel.addItemToDatabase(this, info, container, screenId,
				cellXY[0], cellXY[1], false);

		if (!mRestoring) {
			mWorkspace.addInScreen(view, container, screenId, cellXY[0],
					cellXY[1], 1, 1, isWorkspaceLocked());
		}
	}

	static int[] getSpanForWidget(Context context, ComponentName component,
			int minWidth, int minHeight) {
		Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(context,
				component, null);
		// We want to account for the extra amount of padding that we are adding
		// to the widget
		// to ensure that it gets the full amount of space that it has requested
		int requiredWidth = minWidth + padding.left + padding.right;
		int requiredHeight = minHeight + padding.top + padding.bottom;
		return CellLayout.rectToCell(requiredWidth, requiredHeight, null);
	}

	static int[] getSpanForWidget(Context context, AppWidgetProviderInfo info) {
		return getSpanForWidget(context, info.provider, info.minWidth,
				info.minHeight);
	}

	static int[] getMinSpanForWidget(Context context, AppWidgetProviderInfo info) {
		return getSpanForWidget(context, info.provider, info.minResizeWidth,
				info.minResizeHeight);
	}

	static int[] getSpanForWidget(Context context, PendingAddWidgetInfo info) {
		return getSpanForWidget(context, info.componentName, info.minWidth,
				info.minHeight);
	}

	static int[] getMinSpanForWidget(Context context, PendingAddWidgetInfo info) {
		return getSpanForWidget(context, info.componentName,
				info.minResizeWidth, info.minResizeHeight);
	}

	/**
	 * Add a widget to the workspace.
	 * 
	 * @param appWidgetId
	 *            The app widget id
	 * @param cellInfo
	 *            The position on screen where to create the widget.
	 */
	private void completeAddAppWidget(final int appWidgetId, long container,
			long screenId, AppWidgetHostView hostView,
			AppWidgetProviderInfo appWidgetInfo) {
		if (appWidgetInfo == null) {
			appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
		}
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "completeAddAppWidget: appWidgetId = "
					+ appWidgetId + ", container = " + container
					+ ", screenId = " + screenId);
		}

		// Calculate the grid spans needed to fit this widget
		CellLayout layout = getCellLayout(container, screenId);

		int[] minSpanXY = getMinSpanForWidget(this, appWidgetInfo);
		int[] spanXY = getSpanForWidget(this, appWidgetInfo);

		// Try finding open space on Launcher screen
		// We have saved the position to which the widget was dragged-- this
		// really only matters
		// if we are placing widgets on a "spring-loaded" screen
		int[] cellXY = mTmpAddItemCellCoordinates;
		int[] touchXY = mPendingAddInfo.dropPos;
		int[] finalSpan = new int[2];
		boolean foundCellSpan = false;
		if (mPendingAddInfo.cellX >= 0 && mPendingAddInfo.cellY >= 0) {
			cellXY[0] = mPendingAddInfo.cellX;
			cellXY[1] = mPendingAddInfo.cellY;
			spanXY[0] = mPendingAddInfo.spanX;
			spanXY[1] = mPendingAddInfo.spanY;
			foundCellSpan = true;
		} else if (touchXY != null) {
			// when dragging and dropping, just find the closest free spot
			int[] result = layout.findNearestVacantArea(touchXY[0], touchXY[1],
					minSpanXY[0], minSpanXY[1], spanXY[0], spanXY[1], cellXY,
					finalSpan);
			spanXY[0] = finalSpan[0];
			spanXY[1] = finalSpan[1];
			foundCellSpan = (result != null);
		} else {
			foundCellSpan = layout.findCellForSpan(cellXY, minSpanXY[0],
					minSpanXY[1]);
		}

		if (!foundCellSpan) {
			if (appWidgetId != -1) {
				// Deleting an app widget ID is a void call but writes to disk
				// before returning
				// to the caller...
				new Thread("deleteAppWidgetId") {
					public void run() {
						mAppWidgetHost.deleteAppWidgetId(appWidgetId);
					}
				}.start();
			}
			showOutOfSpaceMessage(isHotseatLayout(layout));
			return;
		}

		// Build Launcher-specific widget info and save to database
		LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(
				appWidgetId, appWidgetInfo.provider);
		launcherInfo.spanX = spanXY[0];
		launcherInfo.spanY = spanXY[1];
		launcherInfo.minSpanX = mPendingAddInfo.minSpanX;
		launcherInfo.minSpanY = mPendingAddInfo.minSpanY;

		LauncherModel.addItemToDatabase(this, launcherInfo, container,
				screenId, cellXY[0], cellXY[1], false);

		if (!mRestoring) {
			if (hostView == null) {
				// Perform actual inflation because we're live
				launcherInfo.hostView = mAppWidgetHost.createView(this,
						appWidgetId, appWidgetInfo);
				launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
			} else {
				// The AppWidgetHostView has already been inflated and
				// instantiated
				launcherInfo.hostView = hostView;
			}
			launcherInfo.hostView.setTag(launcherInfo);
			launcherInfo.hostView.setVisibility(View.VISIBLE);
			launcherInfo.notifyWidgetSizeChanged(this);
			if (launcherInfo.hostView instanceof LauncherAppWidgetHostView) {
				LauncherAppWidgetHostView launcherAppWidgetHostView = (LauncherAppWidgetHostView)launcherInfo.hostView;
				launcherAppWidgetHostView.setObservable(mNifObserables);
				launcherAppWidgetHostView.registerObserver();
			}

			mWorkspace.addInScreen(launcherInfo.hostView, container, screenId,
					cellXY[0], cellXY[1], launcherInfo.spanX,
					launcherInfo.spanY, isWorkspaceLocked());

			addWidgetToAutoAdvanceIfNeeded(launcherInfo.hostView, appWidgetInfo);
		}
		
		
		resetAddInfo();
	}

	//接受主题商店资源下完成的广播
	  BroadcastReceiver  themeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(intent.getAction().equals(ThemeListView.BROADCAST_INTENTFILTER)) {
				String iconPreviewPath = intent.getStringExtra(ThemeListView.KEY_RESOURCE_PREVIEW);
				String name = intent.getStringExtra(ThemeListView.KEY_RESOURCE_NAME);
				String path = intent.getStringExtra(ThemeListView.KEY_RESOURCE_PATH);
				String id = intent.getStringExtra(ThemeListView.KEY_RESOURCE_ID);
				int type =intent.getIntExtra(ThemeListView.KEY_RESOURCE_TYPE, 0);
				
				if(type == 0){//是主题下载完了
					Theme theme = new Theme();
					theme.setIconPreviewPath(iconPreviewPath);
					theme.setStrThemePath(path);
					theme.setThemeName(name);
					theme.setId(id);
					mThemeListView.bindAddThemeItem(theme);
					
				}else{//是壁纸下完了
					Wallpaper wallpaper= new Wallpaper();
					wallpaper.setIconPreviewPath(iconPreviewPath);
					wallpaper.setStrWallpaperPath(path);
					wallpaper.setWallpaperName(name);
					wallpaper.setId(id);
					mWalListView.bindAddWallpaperItem(wallpaper);
				}
				
			}
			else {

				String id = intent.getStringExtra(ThemeListView.KEY_RESOURCE_ID);
				int type =intent.getIntExtra(ThemeListView.KEY_RESOURCE_TYPE, 0);
				if(type ==0) {

					mThemeListView.bindRemove(id);
				}else {

					mWalListView.bindRemove(id);
				}
			}
			
		}
	};

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onAttachedToWindow.");
		}
		FirstFrameAnimatorHelper.initializeDrawListener(getWindow()
				.getDecorView());
		mAttached = true;
		mVisible = true;
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onDetachedFromWindow.");
		}

		mVisible = false;

		if (mAttached) {
			mAttached = false;
		}
		updateRunning();
	}

	public void onWindowVisibilityChanged(int visibility) {
		mVisible = visibility == View.VISIBLE;
		updateRunning();

		//added by yhf for statusbar background
		Intent intent = new Intent("com.android.systemui.statusbar.phone.RELOADBG");
		Log.i(TAG,"onWindowVisibilityChanged,mVisible is :" + visibility);
		if(visibility != View.GONE){
			
			intent.putExtra("type","transparent");
						
		}else{
			intent.putExtra("type","default");
		
		}
		sendBroadcast(intent);

		//end yhf for statusbar background

		
		// The following code used to be in onResume, but it turns out onResume
		// is called when
		// you're in All Apps and click home to go to the workspace.
		// onWindowVisibilityChanged
		// is a more appropriate event to handle
		if (mVisible) {
//			mAppsCustomizeTabHost.onWindowVisible();
			if (!mWorkspaceLoading) {
				final ViewTreeObserver observer = mWorkspace
						.getViewTreeObserver();
				// We want to let Launcher draw itself at least once before we
				// force it to build
				// layers on all the workspace pages, so that transitioning to
				// Launcher from other
				// apps is nice and speedy.
				observer.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
					private boolean mStarted = false;

					public void onDraw() {
						if (mStarted)
							return;
						mStarted = true;
						// We delay the layer building a bit in order to give
						// other message processing a time to run. In particular
						// this avoids a delay in hiding the IME if it was
						// currently shown, because doing that may involve
						// some communication back with the app.
						mWorkspace.postDelayed(mBuildLayersRunnable, 500);
						final ViewTreeObserver.OnDrawListener listener = this;
						mWorkspace.post(new Runnable() {
							public void run() {
								if (mWorkspace != null
										&& mWorkspace.getViewTreeObserver() != null) {
									mWorkspace.getViewTreeObserver()
											.removeOnDrawListener(listener);
								}
							}
						});
						return;
					}
				});
			}
			// When Launcher comes back to foreground, a different Activity
			// might be responsible for
			// the app market intent, so refresh the icon
			if (!DISABLE_MARKET_BUTTON) {
				updateAppMarketIcon();
			}
			clearTypedText();
		}
	}

	private void sendAdvanceMessage(long delay) {
		mHandler.removeMessages(ADVANCE_MSG);
		Message msg = mHandler.obtainMessage(ADVANCE_MSG);
		mHandler.sendMessageDelayed(msg, delay);
		mAutoAdvanceSentTime = System.currentTimeMillis();
	}

	private void updateRunning() {
		boolean autoAdvanceRunning = mVisible && mUserPresent
				&& !mWidgetsToAdvance.isEmpty();
		if (autoAdvanceRunning != mAutoAdvanceRunning) {
			mAutoAdvanceRunning = autoAdvanceRunning;
			if (autoAdvanceRunning) {
				long delay = mAutoAdvanceTimeLeft == -1 ? mAdvanceInterval
						: mAutoAdvanceTimeLeft;
				sendAdvanceMessage(delay);
			} else {
				if (!mWidgetsToAdvance.isEmpty()) {
					mAutoAdvanceTimeLeft = Math
							.max(0,
									mAdvanceInterval
											- (System.currentTimeMillis() - mAutoAdvanceSentTime));
				}
				mHandler.removeMessages(ADVANCE_MSG);
				mHandler.removeMessages(0); // Remove messages sent using
											// postDelayed()
			}
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ADVANCE_MSG) {
				int i = 0;
				for (View key : mWidgetsToAdvance.keySet()) {
					final View v = key
							.findViewById(mWidgetsToAdvance.get(key).autoAdvanceViewId);
					final int delay = mAdvanceStagger * i;
					if (v instanceof Advanceable) {
						postDelayed(new Runnable() {
							public void run() {
								((Advanceable) v).advance();
							}
						}, delay);
					}
					i++;
				}
				sendAdvanceMessage(mAdvanceInterval);
			/*prize-uninstall-xiaxuefeng-2015-8-3-start*/
			} else if (msg.what == UNINSTALL_COMPLETE) {
                if(msg.arg1 == SUCCEEDED) {
                    Toast.makeText(getApplicationContext(), R.string.uninstall_success, Toast.LENGTH_SHORT).show();
                } else {
                	Toast.makeText(getApplicationContext(), R.string.uninstall_failed, Toast.LENGTH_SHORT).show();
                }
			}
			/*prize-uninstall-xiaxuefeng-2015-8-3-end*/
		}
	};
	private Rect mRect;

	void addWidgetToAutoAdvanceIfNeeded(View hostView,
			AppWidgetProviderInfo appWidgetInfo) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "addWidgetToAutoAdvanceIfNeeded hostView = "
					+ hostView + ", appWidgetInfo = " + appWidgetInfo);
		}

		if (appWidgetInfo == null || appWidgetInfo.autoAdvanceViewId == -1)
			return;
		View v = hostView.findViewById(appWidgetInfo.autoAdvanceViewId);
		if (v instanceof Advanceable) {
			mWidgetsToAdvance.put(hostView, appWidgetInfo);
			((Advanceable) v).fyiWillBeAdvancedByHostKThx();
			updateRunning();
		}
	}

	void removeWidgetToAutoAdvance(View hostView) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "removeWidgetToAutoAdvance hostView = "
					+ hostView);
		}

		if (mWidgetsToAdvance.containsKey(hostView)) {
			mWidgetsToAdvance.remove(hostView);
			updateRunning();
		}
	}

	public void removeAppWidget(LauncherAppWidgetInfo launcherInfo) {
		if (LauncherLog.DEBUG) {
			LauncherLog
					.d(TAG, "removeAppWidget launcherInfo = " + launcherInfo);
		}

		removeWidgetToAutoAdvance(launcherInfo.hostView);
		launcherInfo.hostView = null;
	}

	void showOutOfSpaceMessage(boolean isHotseatLayout) {
		int strId = (isHotseatLayout ? R.string.hotseat_out_of_space
				: R.string.out_of_space);
		Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show();
	}

	/**
	 * M: Pop up message allows to you add only one IMtkWidget for the given
	 * AppWidgetInfo.
	 * 
	 * @param info
	 *            The information of the IMtkWidget.
	 */
	void showOnlyOneWidgetMessage(PendingAddWidgetInfo info) {
		try {
			PackageManager pm = getPackageManager();
			String label = pm.getApplicationLabel(
					pm.getApplicationInfo(info.componentName.getPackageName(),
							0)).toString();
			Toast.makeText(this, getString(R.string.one_video_widget, label),
					Toast.LENGTH_SHORT).show();
		} catch (PackageManager.NameNotFoundException e) {
			LauncherLog
					.e(TAG,
							"Got NameNotFounceException when showOnlyOneWidgetMessage.",
							e);
		}
		// Exit spring loaded mode if necessary after adding the widget.
		exitSpringLoadedDragModeDelayed(false, false, null);
	}

	public LauncherAppWidgetHost getAppWidgetHost() {
		return mAppWidgetHost;
	}

	public LauncherModel getModel() {
		return mModel;
	}

	public void closeSystemDialogs() {
		getWindow().closeAllPanels();

		// Whatever we were doing is hereby canceled.
		mWaitingForResult = false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		long startTime = 0;
		if (DEBUG_RESUME_TIME) {
			startTime = System.currentTimeMillis();
		}
//		startUninstallByExlosion(0,0);
//		super.onNewIntent(intent);


		if(mExplosionDialogView.isOpen()) {
			mExplosionDialogView.animateClosed();
			return ;
		}
		
		  if (mWorkspace.getState() == Workspace.State.DRAG_MODEL) {
				mWorkspace.exitDragMode(true, false);
				return ;
			}
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onNewIntent: intent = " + intent);
		}
		if((mWorkspace.isEnterSearchModel() || mHotseat.isEnterSearchModel())&&mWorkspace.isReleaseSearchModel()) {
			this.getworkspace().extEnterSearchModel();
			return;
		}
		// Close the menu
		if (Intent.ACTION_MAIN.equals(intent.getAction())) {
			// also will cancel mWaitingForResult.
			closeSystemDialogs();

			final boolean alreadyOnHome = mHasFocus
					&& ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

			if (mWorkspace == null) {
				// Can be cases where mWorkspace is null, this prevents a NPE
				LauncherLog.d(TAG,
						"onNewIntent processIntent run() mWorkspace == null");
				return;
			}
			Folder openFolder = mWorkspace.getOpenFolder();
			// In all these cases, only animate if we're already on home
			mWorkspace.exitWidgetResizeMode();
			if (alreadyOnHome && mState == State.WORKSPACE
					&& !mWorkspace.isTouchActive() && openFolder == null) {
				// / M: Call the appropriate callback for the IMtkWidget on the
				// current page
				// / when press "Home" key move to default screen.
				mWorkspace.moveOutAppWidget(mWorkspace.getCurrentPage());

				mWorkspace.moveToDefaultScreen(true);
			}

			closeFolder();
			mWorkspace.onBackpressed();
			exitSpringLoadedDragMode(true);

			// If we are already on home, then just animate back to the
			// workspace,
			// otherwise, just wait until onResume to set the state back to
			// Workspace
			if (alreadyOnHome) {
				showWorkspace(true);
			} else {
				mOnResumeState = State.WORKSPACE;
			}

			final View v = getWindow().peekDecorView();
			if (v != null && v.getWindowToken() != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

			// Reset the apps customize page
			// / M: When already onhome, don't reset page to app page refering
			// to launcher2.
//			if (!alreadyOnHome && mAppsCustomizeTabHost != null) {
//				mAppsCustomizeTabHost.reset();
//			}

			// / M: need to exit edit mode if needed, for op09.
			if (isInEditMode()) {
				exitEditMode();
			}
		}

		if (DEBUG_RESUME_TIME) {
			Log.d(TAG,
					"Time spent in onNewIntent: "
							+ (System.currentTimeMillis() - startTime));
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);

		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onRestoreInstanceState: state = " + state
					+ ", mSavedInstanceState = " + mSavedInstanceState);
		}

		for (int page : mSynchronouslyBoundPages) {
			mWorkspace.restoreInstanceStateForChild(page);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (mWorkspace.getChildCount() > 0) {
			outState.putInt(RUNTIME_STATE_CURRENT_SCREEN,
					mWorkspace.getRestorePage());
		}
		super.onSaveInstanceState(outState);

		outState.putInt(RUNTIME_STATE, mState.ordinal());
		// We close any open folder since it will not be re-opened, and we need
		// to make sure
		// this state is reflected.
		closeFolder();

		if (mPendingAddInfo.container != ItemInfo.NO_ID
				&& mPendingAddInfo.screenId > -1 && mWaitingForResult) {
			outState.putLong(RUNTIME_STATE_PENDING_ADD_CONTAINER,
					mPendingAddInfo.container);
			outState.putLong(RUNTIME_STATE_PENDING_ADD_SCREEN,
					mPendingAddInfo.screenId);
			outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_X,
					mPendingAddInfo.cellX);
			outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_Y,
					mPendingAddInfo.cellY);
			outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_X,
					mPendingAddInfo.spanX);
			outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y,
					mPendingAddInfo.spanY);
			outState.putParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO,
					mPendingAddWidgetInfo);
		}

		if (mFolderInfo != null && mWaitingForResult) {
			outState.putBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, true);
			outState.putLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID,
					mFolderInfo.id);
		}

		// Save the current AppsCustomize tab
		/*if (mAppsCustomizeTabHost != null) {
			String currentTabTag = mAppsCustomizeTabHost.getCurrentTabTag();
			if (currentTabTag != null) {
				outState.putString("apps_customize_currentTab", currentTabTag);
			}
//			int currentIndex = mAppsCustomizeContent
//					.getSaveInstanceStateIndex();
//			outState.putInt("apps_customize_currentIndex", currentIndex);
		}*/
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, " onSaveInstanceState: outState = " + outState);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "(Launcher)onDestroy: this = " + this);
		}
		// Remove all pending runnables
		unregisterThemeReceiver();
		mHandler.removeMessages(ADVANCE_MSG);
		mHandler.removeMessages(0);
		mWorkspace.removeCallbacks(mBuildLayersRunnable);
	//add by zhouerlong begin 20150812
	//add by zhouerlong end 20150812

		// Stop callbacks from LauncherModel
		LauncherAppState app = (LauncherAppState.getInstance());
		mModel.stopLoader();
		app.setLauncher(null);
		unregisterBatteryReceiver();//add by zhouerlong
		try {
			mAppWidgetHost.stopListening();
		} catch (NullPointerException ex) {
			Log.w(TAG,
					"problem while stopping AppWidgetHost during Launcher destruction",
					ex);
		}
		mAppWidgetHost = null;

		mWidgetsToAdvance.clear();

		TextKeyListener.getInstance().release();

		// Disconnect any of the callbacks and drawables associated with
		// ItemInfos on the workspace
		// to prevent leaking Launcher activities on orientation change.
		if (mModel != null) {
			mModel.unbindItemInfosAndClearQueuedBindRunnables();
		}

		getContentResolver().unregisterContentObserver(mWidgetObserver);
		unregisterReceiver(mCloseSystemDialogsReceiver);

		mDragLayer.clearAllResizeFrames();
		((ViewGroup) mWorkspace.getParent()).removeAllViews();
		mWorkspace.removeAllViews();
		mWorkspace = null;
		mDragController = null;

		LauncherAnimUtils.onDestroyActivity();
        
        /**M: added for unread feature, load and bind unread info.@{**/
        if (getResources().getBoolean(R.bool.config_unreadSupport)) {
            if(mUnreadLoader != null){
                mUnreadLoader.initialize(null);
            }
        }
        /**@}**/
	}

	public DragController getDragController() {
		return mDragController;
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if (requestCode >= 0)
			mWaitingForResult = true;
		super.startActivityForResult(intent, requestCode);
	}

	/**
	 * Indicates that we want global search for this activity by setting the
	 * globalSearch argument for {@link #startSearch} to true.
	 */
	@Override
	public void startSearch(String initialQuery, boolean selectInitialQuery,
			Bundle appSearchData, boolean globalSearch) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "startSearch.");
		}
		showWorkspace(true);

		if (initialQuery == null) {
			// Use any text typed in the launcher as the initial query
			initialQuery = getTypedText();
		}
		if (appSearchData == null) {
			appSearchData = new Bundle();
			appSearchData.putString("source", "launcher-search");
		}
		Rect sourceBounds = new Rect();
		if (mSearchDropTargetBar != null) {
			sourceBounds = mSearchDropTargetBar.getSearchBarBounds();
		}

		startSearch(initialQuery, selectInitialQuery, appSearchData,
				sourceBounds);
	}

	public void startSearch(String initialQuery, boolean selectInitialQuery,
			Bundle appSearchData, Rect sourceBounds) {
		startGlobalSearch(initialQuery, selectInitialQuery, appSearchData,
				sourceBounds);
	}

	/**
	 * Starts the global search activity. This code is a copied from
	 * SearchManager
	 */
	private void startGlobalSearch(String initialQuery,
			boolean selectInitialQuery, Bundle appSearchData, Rect sourceBounds) {
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		ComponentName globalSearchActivity = searchManager
				.getGlobalSearchActivity();
		if (globalSearchActivity == null) {
			Log.w(TAG, "No global search activity found.");
			return;
		}
		Intent intent = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(globalSearchActivity);
		// Make sure that we have a Bundle to put source in
		if (appSearchData == null) {
			appSearchData = new Bundle();
		} else {
			appSearchData = new Bundle(appSearchData);
		}
		// Set source to package name of app that starts global search, if not
		// set already.
		if (!appSearchData.containsKey("source")) {
			appSearchData.putString("source", getPackageName());
		}
		intent.putExtra(SearchManager.APP_DATA, appSearchData);
		if (!TextUtils.isEmpty(initialQuery)) {
			intent.putExtra(SearchManager.QUERY, initialQuery);
		}
		if (selectInitialQuery) {
			intent.putExtra(SearchManager.EXTRA_SELECT_QUERY,
					selectInitialQuery);
		}
		intent.setSourceBounds(sourceBounds);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "Global search activity not found: "
					+ globalSearchActivity);
		}
	}

	@Override
	public boolean onSearchRequested() {
		startSearch(null, false, null, true);
		// Use a custom animation for launching search
		return true;
	}

	public boolean isWorkspaceLocked() {
		return mWorkspaceLoading || mWaitingForResult;
	}

	private void resetAddInfo() {
		mPendingAddInfo.container = ItemInfo.NO_ID;
		mPendingAddInfo.screenId = -1;
		mPendingAddInfo.cellX = mPendingAddInfo.cellY = -1;
		mPendingAddInfo.spanX = mPendingAddInfo.spanY = -1;
		mPendingAddInfo.minSpanX = mPendingAddInfo.minSpanY = -1;
		mPendingAddInfo.dropPos = null;
	}

	void addAppWidgetImpl(final int appWidgetId, ItemInfo info,
			AppWidgetHostView boundWidget, AppWidgetProviderInfo appWidgetInfo) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "addAppWidgetImpl: appWidgetId = " + appWidgetId
					+ ", info = " + info + ", boundWidget = " + boundWidget
					+ ", appWidgetInfo = " + appWidgetInfo);
		}

		if (appWidgetInfo.configure != null) {
			mPendingAddWidgetInfo = appWidgetInfo;

			// Launch over to configure widget, if needed
			Intent intent = new Intent(
					AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.setComponent(appWidgetInfo.configure);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			Utilities.startActivityForResultSafely(this, intent,
					REQUEST_CREATE_APPWIDGET);
		} else {
			// Otherwise just add it
			completeAddAppWidget(appWidgetId, info.container, info.screenId,
					boundWidget, appWidgetInfo);
			// Exit spring loaded mode if necessary after adding the widget
			exitSpringLoadedDragModeDelayed(true, false, null);
			mWorkspace.setAddAppWidgetSuccessfulDrop();
		}
	}

	protected void moveToCustomContentScreen(boolean animate) {
		// Close any folders that may be open.
		closeFolder();
		mWorkspace.moveToCustomContentScreen(animate);
	}

	/**
	 * Process a shortcut drop.
	 * 
	 * @param componentName
	 *            The name of the component
	 * @param screenId
	 *            The ID of the screen where it should be added
	 * @param cell
	 *            The cell it should be added to, optional
	 * @param position
	 *            The location on the screen where it was dropped, optional
	 */
	void processShortcutFromDrop(ComponentName componentName, long container,
			long screenId, int[] cell, int[] loc) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "processShortcutFromDrop componentName = "
					+ componentName + ", container = " + container
					+ ", screenId = " + screenId);
		}

		resetAddInfo();
		mPendingAddInfo.container = container;
		mPendingAddInfo.screenId = screenId;
		mPendingAddInfo.dropPos = loc;

		if (cell != null) {
			mPendingAddInfo.cellX = cell[0];
			mPendingAddInfo.cellY = cell[1];
		}

		Intent createShortcutIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
		createShortcutIntent.setComponent(componentName);
		processShortcut(createShortcutIntent);
	}

	/**
	 * Process a widget drop.
	 * 
	 * @param info
	 *            The PendingAppWidgetInfo of the widget being added.
	 * @param screenId
	 *            The ID of the screen where it should be added
	 * @param cell
	 *            The cell it should be added to, optional
	 * @param position
	 *            The location on the screen where it was dropped, optional
	 */
	void addAppWidgetFromDrop(PendingAddWidgetInfo info, long container,
			long screenId, int[] cell, int[] span, int[] loc) {
		if (LauncherLog.DEBUG) {
			LauncherLog
					.d(TAG, "addAppWidgetFromDrop: info = " + info
							+ ", container = " + container + ", screenId = "
							+ screenId);
		}

		resetAddInfo();
		mPendingAddInfo.container = info.container = container;
		mPendingAddInfo.screenId = info.screenId = screenId;
		mPendingAddInfo.dropPos = loc;
		mPendingAddInfo.minSpanX = info.minSpanX;
		mPendingAddInfo.minSpanY = info.minSpanY;

		if (cell != null) {
			mPendingAddInfo.cellX = cell[0];
			mPendingAddInfo.cellY = cell[1];
		}
		if (span != null) {
			mPendingAddInfo.spanX = span[0];
			mPendingAddInfo.spanY = span[1];
		}

		AppWidgetHostView hostView = info.boundWidget;
		int appWidgetId;
		if (hostView != null) {
			appWidgetId = hostView.getAppWidgetId();
			addAppWidgetImpl(appWidgetId, info, hostView, info.info);
		} else {
			// In this case, we either need to start an activity to get
			// permission to bind
			// the widget, or we need to start an activity to configure the
			// widget, or both.
			appWidgetId = getAppWidgetHost().allocateAppWidgetId();
			Bundle options = info.bindOptions;

			boolean success = false;
			if (options != null) {
				success = mAppWidgetManager.bindAppWidgetIdIfAllowed(
						appWidgetId, info.componentName, options);
			} else {
				success = mAppWidgetManager.bindAppWidgetIdIfAllowed(
						appWidgetId, info.componentName);
			}
			if (success) {
				addAppWidgetImpl(appWidgetId, info, null, info.info);
			} else {
				mPendingAddWidgetInfo = info.info;
				Intent intent = new Intent(
						AppWidgetManager.ACTION_APPWIDGET_BIND);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						appWidgetId);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,
						info.componentName);
				// TODO: we need to make sure that this accounts for the options
				// bundle.
				// intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS,
				// options);
				startActivityForResult(intent, REQUEST_BIND_APPWIDGET);
			}
		}
	}

	void processShortcut(Intent intent) {
		// Handle case where user selected "Applications"
		String applicationName = getResources().getString(
				R.string.group_applications);
		String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "processShortcut: applicationName = "
					+ applicationName + ", shortcutName = " + shortcutName
					+ ", intent = " + intent);
		}

		if (applicationName != null && applicationName.equals(shortcutName)) {
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

			Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
			pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
			pickIntent.putExtra(Intent.EXTRA_TITLE,
					getText(R.string.title_select_application));
			Utilities.startActivityForResultSafely(this, pickIntent,
					REQUEST_PICK_APPLICATION);
		} else {
			Utilities.startActivityForResultSafely(this, intent,
					REQUEST_CREATE_SHORTCUT);
		}
	}

	void processWallpaper(Intent intent) {
		startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
	}

	FolderIcon addFolder(CellLayout layout, long container,
			final long screenId, int cellX, int cellY) {
		final FolderInfo folderInfo = new FolderInfo();
		folderInfo.title = getText(R.string.folder_name);

		// Update the model
		LauncherModel.addItemToDatabase(Launcher.this, folderInfo, container,
				screenId, cellX, cellY, false);
		sFolders.put(folderInfo.id, folderInfo);

		// Create the view
		FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this,
				layout, folderInfo, mIconCache);
	//add by zhouerlong 20150808 begin
		newFolder.setmNiftyObserables(mNifObserables);
	//add by zhouerlong 20150808 end
		mWorkspace.addInScreen(newFolder, container, screenId, cellX, cellY, 1,
				1, isWorkspaceLocked());
		// Force measure the new folder icon
		CellLayout parent = mWorkspace.getParentCellLayoutForView(newFolder);
		parent.getShortcutsAndWidgets().measureChild(newFolder);
		return newFolder;
	}

	void removeFolder(FolderInfo folder) {
		sFolders.remove(folder.id);
	}

	protected void startWallpaper() {
		final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
		pickWallpaper.setComponent(getWallpaperPickerComponent());
		startActivityForResult(pickWallpaper, REQUEST_PICK_WALLPAPER);
	}

	protected ComponentName getWallpaperPickerComponent() {
		return new ComponentName(getPackageName(),
				WallpaperPickerActivity.class.getName());
	}

	/**
	 * Registers various content observers. The current implementation registers
	 * only a favorites observer to keep track of the favorites applications.
	 */
	private void registerContentObservers() {
		ContentResolver resolver = getContentResolver();
		resolver.registerContentObserver(
				LauncherProvider.CONTENT_APPWIDGET_RESET_URI, true,
				mWidgetObserver);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (LauncherLog.DEBUG_KEY) {
			LauncherLog.d(TAG, "dispatchKeyEvent: keyEvent = " + event);
		}

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_HOME:
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (isPropertyEnabled(DUMP_STATE_PROPERTY)) {
					dumpState();
					return true;
				}
				break;
			}
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_HOME:
				return true;
			}
		}
		//光感问题
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_PAGE_DOWN:
			mWorkspace.snapToLeftPage(false);
			break;
		case KeyEvent.KEYCODE_PAGE_UP:
			mWorkspace.snapToRightPage(false);
			break;
		default:
			break;
		}

		return super.dispatchKeyEvent(event);
	}
	
  /**M: Added for unread message feature.@{**/
    
    /**
     * M: Bind component unread information in workspace and all apps list.
     *
     * @param component the component name of the app.
     * @param unreadNum the number of the unread message.
     */
    public void bindComponentUnreadChanged(final ComponentName component, final int unreadNum,final String title) {
        if (LauncherLog.DEBUG_UNREAD) {
            LauncherLog.d(TAG, "bindComponentUnreadChanged: component = " + component
                    + ", unreadNum = " + unreadNum + ", this = " + this);
        }
        // Post to message queue to avoid possible ANR.
        mHandler.post(new Runnable() {
            public void run() {
                final long start = System.currentTimeMillis();
                if (LauncherLog.DEBUG_PERFORMANCE) {
                    LauncherLog.d(TAG, "bindComponentUnreadChanged begin: component = " + component
                            + ", unreadNum = " + unreadNum + ", start = " + start);
                }
                if (mWorkspace != null) {
                    mWorkspace.updateComponentUnreadChanged(component, unreadNum,title);
                }

//                if (mAppsCustomizeContent != null) {
//                    mAppsCustomizeContent.updateAppsUnreadChanged(component, unreadNum);
//                }
                if (LauncherLog.DEBUG_PERFORMANCE) {
                    LauncherLog.d(TAG, "bindComponentUnreadChanged end: current time = "
                            + System.currentTimeMillis() + ", time used = "
                            + (System.currentTimeMillis() - start));
                }
            }
        });
    }

   /**
     * M: Bind shortcuts unread number if binding process has finished.
     */
    public void bindUnreadInfoIfNeeded() {
        if (LauncherLog.DEBUG_UNREAD) {
            LauncherLog.d(TAG, "bindUnreadInfoIfNeeded: mBindingWorkspaceFinished = "
                    + mBindingWorkspaceFinished + ", thread = " + Thread.currentThread());
        }
        if (mBindingWorkspaceFinished) {
            bindWorkspaceUnreadInfo();
        }

        if (mBindingAppsFinished) {
            bindAppsUnreadInfo();
        }
        mUnreadLoadCompleted = true;
    }
    
    
    public void bindFirstInstallInfo() {
    	
    }

    /**
     * M: Bind unread number to shortcuts with data in MTKUnreadLoader.
     */
    private void bindWorkspaceUnreadInfo() {
        mHandler.post(new Runnable() {
            public void run() {
                final long start = System.currentTimeMillis();
                if (LauncherLog.DEBUG_PERFORMANCE) {
                    LauncherLog.d(TAG, "bindWorkspaceUnreadInfo begin: start = " + start);
                }
                if (mWorkspace != null) {
                    mWorkspace.updateShortcutsAndFoldersUnread();
                }
                if (LauncherLog.DEBUG_PERFORMANCE) {
                    LauncherLog.d(TAG, "bindWorkspaceUnreadInfo end: current time = "
                            + System.currentTimeMillis() + ",time used = "
                            + (System.currentTimeMillis() - start));
                }
            }
        });
    }

	//add by zhouerlong begin 20150814
	/**绑定文件夹删除的图标到workspace
	 * @param info
	 */
	public void bindFolderItemsToAddWorkspace(ItemInfo info) {
		mWorkspace.bindFolderItemsToAddWorkspace(info);
	}
	//add by zhouerlong end 20150814
	
    /**
     * M: Bind unread number to shortcuts with data in MTKUnreadLoader.
     */
    private void bindAppsUnreadInfo() {
        mHandler.post(new Runnable() {
            public void run() {
                final long start = System.currentTimeMillis();
                if (LauncherLog.DEBUG_PERFORMANCE) {
                    LauncherLog.d(TAG, "bindAppsUnreadInfo begin: start = " + start);
                }
//                if (mAppsCustomizeContent != null) {
//                    mAppsCustomizeContent.updateAppsUnread();
//                }
                if (LauncherLog.DEBUG_PERFORMANCE) {
                    LauncherLog.d(TAG, "bindAppsUnreadInfo end: current time = "
                            + System.currentTimeMillis() + ",time used = "
                            + (System.currentTimeMillis() - start));
                }
            }
        });
    }
    
    /**@}**/
	
	@Override
	public void onBackPressed() {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "Back key pressed, mState = " + mState
					+ ", mOnResumeState = " + mOnResumeState);
		}
		if(mExplosionDialogView.isOpen()) {
			mExplosionDialogView.animateClosed();
			return ;
		}
		if (isAllAppsVisible()) {
			// / M: exit edit mode if the apps customize pane is in edit mode,
			// for op09.
			if (isInEditMode()) {
				exitEditMode();
			} else {
				if (/*mAppsCustomizeContent.getContentType() == AppsCustomizePagedView.ContentType.Applications*/false) {
					if (mWorkspace.getOpenFolder() != null) {
						Folder openFolder = mWorkspace.getOpenFolder();
						if (openFolder.isEditingName()) {
							openFolder.dismissEditingName();
						}else if (mDragLayer.indexOfChild(mHideView)!=-1){
			            		 //如果当前 编辑文件夹的状态 先关闭 编辑文件夹界面
							mHideView.animateClosed();
							
						} else {
							closeFolder();
						}
					} else {
						if (AppsCustomizePagedView.DISABLE_ALL_APPS) {
							if((mWorkspace.isEnterSearchModel() || mHotseat.isEnterSearchModel())&&mWorkspace.isReleaseSearchModel()) {
								mWorkspace.extEnterSearchModel();
							}
							if (mWorkspace.isInSpringLoadMoed()) {
								if (isEnterEdit()) {
									this.setupChangeAnimation(SpringState.NONE, 400, Launcher.BACK_OVER_SPRING_ID);
								}else {
									mWorkspace.exitSpringLoadMode(true,false);
									mState = State.WORKSPACE;
								}
							}else if (mWorkspace.isInDragModed()) {
								mWorkspace.exitDragMode(true, false);
							}else {
								showWorkspace(true);
								
							}
						}
						else {
							showWorkspace(true);
						}
					}
				} else {
					// add by zhouerlong
					if (mWorkspace.getOpenFolder() != null) {
						Folder openFolder = mWorkspace.getOpenFolder();
						if (openFolder.isEditingName()) {
							openFolder.dismissEditingName();
						} else {
							closeFolder();
						}
					}// add by zhouerlong
					else if (mWorkspace.isEnterSearchModel()
							&& mWorkspace.isReleaseSearchModel()) {
						mWorkspace.extEnterSearchModel();
					}else if (mHotseat.isEnterSearchModel()) {
						mHotseat.extEnterSearchModel();
					}
					else if (mWorkspace.isEnterSearchModel()
							&& mWorkspace.isReleaseSearchModel()&&isSupportT9Search) {
						mWorkspace.extEnterSearchModel();
					}
					else if (mWorkspace.isInSpringLoadMoed()) {
						if (AppsCustomizePagedView.DISABLE_ALL_APPS) {
						if (isEnterEdit()) {
							this.setupChangeAnimation(SpringState.NONE, 400, 0);
						}else {
							mWorkspace.onBackpressed();
							mWorkspace.exitSpringLoadMode(true,false);
							mState = State.WORKSPACE;
						}
						}else {
							mWorkspace.exitSpringLoadMode(true,false);
							mState = State.WORKSPACE;
						}
					} else if (mWorkspace.getState() == Workspace.State.DRAG_MODEL) {
						mWorkspace.exitDragMode(true, false);
					} else if (mWorkspace.isInOverviewMode()) {
						showOverviewMode(true);
					} else if (mDragLayer.indexOfChild(mVisibleView) != -1) {
						mVisibleView.closeVisibleView();
					} else if (mDragLayer.indexOfChild(mHideView) != -1) {
						mHideView.closeHideView();
					}
				}
			}
		} else if (mWorkspace.isInOverviewMode()) {
			mWorkspace.exitOverviewMode(true);
		} else if (mWorkspace.isInSpringLoadMoed()) {
			mWorkspace.exitSpringLoadMode(true,false);
			// mo by zhouerlong
		} else if (mWorkspace.getOpenFolder() != null) {
			Folder openFolder = mWorkspace.getOpenFolder();
			if (openFolder.isEditingName()) {
				openFolder.dismissEditingName();
			} else {
				closeFolder();
			}
		} else {
			mWorkspace.exitWidgetResizeMode();

			// Back button is a no-op here, but give at least some feedback for
			// the button press
			mWorkspace.showOutlinesTemporarily();
		}
	}

	/**
	 * Re-listen when widgets are reset.
	 */
	private void onAppWidgetReset() {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onAppWidgetReset.");
		}

		if (mAppWidgetHost != null) {
			mAppWidgetHost.startListening();
		}
	}
	
	public void removeCellItem(View v) {
		CellLayout parentCell = this.getWorkspace().getParentCellLayoutForView(v);
		if (parentCell != null) {
			parentCell.removeView(v);

            LauncherModel.deleteItemFromDatabase(this, (ItemInfo)v.getTag());
		}
	}
	
	
	

	/**
	 * Launches the intent referred by the clicked shortcut.
	 * 
	 * @param v
	 *            The view representing the clicked shortcut.
	 */
	public void onClick(View v) {
		// Make sure that rogue clicks don't get through while allapps is
		// launching, or after the
		// view has detached (it's possible for this to happen if the view is
		// removed mid touch).

		// / M: add systrace to analyze application launche time.
		Trace.traceBegin(Trace.TRACE_TAG_INPUT, "Launcher.onClick");

		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "Click on view " + v);
		}
		
		if(mExplosionDialogView.isOpen()) {
			return ;
		}
		if (v.getWindowToken() == null) {
			LauncherLog.d(TAG,
					"Click on a view with no window token, directly return.");
			return;
		}

		if (!mWorkspace.isFinishedSwitchingState()) {
			LauncherLog
					.d(TAG,
							"The workspace is in switching state when clicking on view, directly return.");
			return;
		}

		//文件夹问题
		/*if (mWorkspace.indexofFolder() != -1) {
			if (v.getParent().getParent().getParent().getParent().getParent() instanceof Folder) {
				
			}else {
				return;
			}
		}*/
		if (v instanceof Workspace) {
			if (mWorkspace.isInOverviewMode()) {
				mWorkspace.exitOverviewMode(true);
			}
			// add by zhouerlong
			/*if (mWorkspace.isInSpringLoadMoed()) {
				mWorkspace.exitSpringLoadMode(true);
			}
			// add by zhouerlong
			return;*/
		}
		// add by zhouerlong
		

		
		
		// add by zhouerlong
		if (v instanceof CellLayout) {
			if (mWorkspace.isInOverviewMode()) {
				mWorkspace.exitOverviewMode(mWorkspace.indexOfChild(v), true);
			}
			// add by zhouerlong
			if (mWorkspace.isInSpringLoadMoed()) {
				mWorkspace.exitSpringLoadMode(true, false);
			}
			
			if(mWorkspace.isInDragModed()) {
				mWorkspace.exitDragMode(true, false);
			}
			// add by zhouerlong
		}

		Object tag = v.getTag();
		if (tag instanceof ShortcutInfo) {
			// Open shortcut
			final ShortcutInfo shortcut = (ShortcutInfo) tag;
			final Intent intent = shortcut.intent;
		//	FileUtils.saveFile(intent.getComponent().toString());
			// Check for special shortcuts
			if (shortcut.fromAppStore == 1) {
				DownLoadService downLoadService = mModel.getDownLoadService();
				if ( downLoadService!= null) {
					int state = shortcut.down_state;
					state = state != DownLoadService.STATE_DOWNLOAD_PAUSE ? DownLoadService.STATE_DOWNLOAD_PAUSE
							: DownLoadService.STATE_DOWNLOAD_START_LOADING;
					shortcut.down_state = state;
					downLoadService.pikerDownLoadState(shortcut.packageName,
							state);
				}
				return;
			}
			if (intent.getComponent() != null) {
				final String shortcutClass = intent.getComponent()
						.getClassName();

				if (shortcutClass.equals(WidgetAdder.class.getName())) {
					showAllApps(true,
							AppsCustomizePagedView.ContentType.Widgets, true);
					return;
				} else if (shortcutClass.equals(MemoryDumpActivity.class
						.getName())) {
					MemoryDumpActivity.startDump(this);
					return;
				} else if (shortcutClass.equals(ToggleWeightWatcher.class
						.getName())) {
					toggleShowWeightWatcher();
					return;
				}
			}

			// Start activities
			int[] pos = new int[2];
			v.getLocationOnScreen(pos);
			intent.setSourceBounds(new Rect(pos[0], pos[1], pos[0]
					+ v.getWidth(), pos[1] + v.getHeight()));
			// add by zhouerlong
			boolean success = false;
			if (this.getWorkspace().isInDragModed()&&mSpringState != SpringState.BATCH_EDIT_APPS) {
	//add by zhouerlong 20150808 begin
//				removeCellItem(v);
				/*
				 * 进入卸载模式
				 */

			/*	if (!((shortcut.flags & AppInfo.DOWNLOADED_FLAG) == 0)) {
	            mExplosionField.explode(v,null);
				}
				startApplicationUninstallActivity(intent.getComponent(),shortcut.flags);*/
	//add by zhouerlong 20150808 end
			} else if (mSpringState == SpringState.BATCH_EDIT_APPS) {
				CellLayout layout = (CellLayout) v.getParent().getParent();
				if (!isHotseatLayout(layout)) {
					if (shortcut.mItemState != ItemInfo.State.BATCH_SELECT_MODEL) {

						shortcut.mItemState = ItemInfo.State.BATCH_SELECT_MODEL;
					} else {
						shortcut.mItemState = ItemInfo.State.NONE;
					}
					mWorkspace.onMulitipleDragClick(v, shortcut);
					mMulEditNagiration.togle(mWorkspace.getMultipleDragViews().size());
					BubbleTextView b = (BubbleTextView) v;
//					b.onChanged();
					if (layout.getPrizeNavigationView() !=null)
					layout.getPrizeNavigationView().invalidate();
					
					v.requestLayout();
				}
				
			} else  {
				
				
				success = startActivitySafely(v, intent, tag);
				if(success) {
					
				/*	ContentValues content = new ContentValues();
					content.put(FirstInstallTable.FIRST_INSTALL, 0);
					String whereClause = FirstInstallTable.PACKAGE_NAME+"=?";
					String whereArgs[] = new String[]{intent.getPackage()};
					LauncherProvider.update(FirstInstallTable.FIRST_INSTALL_TABLE, content, whereClause, whereArgs);
					shortcut.firstInstall = 0;*/
					shortcut.firstInstall=0;
					LauncherModel.modifyItemInDatabaseByFirstInstall(this, shortcut);
					v.invalidate();
					
				}
			}
			// add by zhouerlong

			mStats.recordLaunch(intent, shortcut);

			if (success && v instanceof BubbleTextView) {
				mWaitingForResume = (BubbleTextView) v;
				mWaitingForResume.setStayPressed(true);
			}
		} else if (tag instanceof FolderInfo) {
        	FolderInfo info = (FolderInfo) tag;
            if (v instanceof FolderIcon && !info.opened) {
				FolderIcon fi = (FolderIcon) v;
				
				if (mSpringState == SpringState.BATCH_EDIT_APPS) {
					if (info.mItemState != ItemInfo.State.BATCH_SELECT_MODEL) {

						info.mItemState = ItemInfo.State.BATCH_SELECT_MODEL;
					} else {
						info.mItemState = ItemInfo.State.NONE;
					}
					CellLayout layout = (CellLayout) v.getParent().getParent();
					layout.getPrizeNavigationView().invalidate();
					mWorkspace.onMulitipleDragClick(v, info);
					mMulEditNagiration.togle(mWorkspace.getMultipleDragViews().size());
//					View b = (BubbleTextView) v;
//					b.onChanged();
					v.requestLayout();
					((FolderIcon)v).mPreviewBackground.invalidate();
					
				}/*
				if (this.getWorkspace().isInSpringLoadMoed()&&!AppsCustomizePagedView.DISABLE_ALL_APPS) {
					removeCellItem(v);
				}*/else {
					handleFolderClick(fi);
				}
			}
		} else if (v == mAllAppsButton) {
			if (isAllAppsVisible()) {
				showWorkspace(true);
			} else {
				onClickAllAppsButton(v);
			}
		}

		// / M: add systrace to analyze application launche time.
		Trace.traceEnd(Trace.TRACE_TAG_INPUT);
	}
	
	
	public void explosition(View v,Runnable r) {
		
        mExplosionField.explode(v,r);
	}
	//?????????
	public boolean isEditAppsModel() {
		return this.mNeedSwimming&&!this.getWorkspace().isInSpringLoadMoed();
	}

	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	/**
	 * Event handler for the search button
	 * 
	 * @param v
	 *            The view that was clicked.
	 */
	public void onClickSearchButton(View v) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onClickSearchButton v = " + v);
		}

		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

		onSearchRequested();
	}

	/**
	 * Event handler for the voice button
	 * 
	 * @param v
	 *            The view that was clicked.
	 */
	public void onClickVoiceButton(View v) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onClickVoiceButton v = " + v);
		}

		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

		startVoice();
	}

	public void startVoice() {
		try {
			final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			ComponentName activityName = searchManager
					.getGlobalSearchActivity();
			Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (activityName != null) {
				intent.setPackage(activityName.getPackageName());
			}
			startActivity(null, intent, "onClickVoiceButton");
		} catch (ActivityNotFoundException e) {
			Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivitySafely(null, intent, "onClickVoiceButton");
		}
	}

	/**
	 * Event handler for the "grid" button that appears on the home screen,
	 * which enters all apps mode.
	 * 
	 * @param v
	 *            The view that was clicked.
	 */
	public void onClickAllAppsButton(View v) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG,
					"[All apps launch time][Start] onClickAllAppsButton.");
		}
//文件夹冲突问题
		if (mWorkspace.indexofFolder() != -1) {
			return;
		}
		showAllApps(true, AppsCustomizePagedView.ContentType.Applications, true);
	}

	public void onTouchDownAllAppsButton(View v) {
		// Provide the same haptic feedback that the system offers for virtual
		// keys.
		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	}

	public void performHapticFeedbackOnTouchDown(View v) {
		// Provide the same haptic feedback that the system offers for virtual
		// keys.
		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	}

	public View.OnTouchListener getHapticFeedbackTouchListener() {
		if (mHapticFeedbackTouchListener == null) {
			mHapticFeedbackTouchListener = new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
						v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					}
					return false;
				}
			};
		}
		return mHapticFeedbackTouchListener;
	}

	public void onClickAppMarketButton(View v) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onClickAppMarketButton v = " + v
					+ ", mAppMarketIntent = " + mAppMarketIntent);
		}

		if (!DISABLE_MARKET_BUTTON) {
			if (mAppMarketIntent != null) {
				startActivitySafely(v, mAppMarketIntent, "app market");
			} else {
				Log.e(TAG, "Invalid app market intent.");
			}
		}
	}

	/**
	 * Called when the user stops interacting with the launcher. This implies
	 * that the user is now on the homescreen and is not doing housekeeping.
	 */
	protected void onInteractionEnd() {
	}

	/**
	 * Called when the user starts interacting with the launcher. The possible
	 * interactions are: - open all apps - reorder an app shortcut, or a widget
	 * - open the overview mode. This is a good time to stop doing things that
	 * only make sense when the user is on the homescreen and not doing
	 * housekeeping.
	 */
	protected void onInteractionBegin() {
	}

	void startApplicationDetailsActivity(ComponentName componentName) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG,
					"startApplicationDetailsActivity: componentName = "
							+ componentName);
		}

		String packageName = componentName.getPackageName();
		Intent intent = new Intent(
				Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts(
						"package", packageName, null));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivitySafely(null, intent, "startApplicationDetailsActivity");
	}

	
	public void startUninstallByExlosion(ItemInfo iteminfo,Runnable r) {

		if (mExplosionDialogView.getParent() == null) {

			DragLayer.LayoutParams lp = new DragLayer.LayoutParams(0, 0);
			mDragLayer.addView(mExplosionDialogView, lp);
			if (iteminfo instanceof ShortcutInfo) {

				mExplosionDialogView.applyFromShortcutInfo(mIconCache,
						(ShortcutInfo) iteminfo);
			}

		}
		mExplosionDialogView.setProgressByWorkspace(mProgress);
		mExplosionDialogView.animateOpen(r);
	}
	
	public void revertExplosion() {
		mExplosionDialogView.animateClosed();
	}
	// returns true if the activity was started
	boolean startApplicationUninstallActivity(ComponentName componentName,
			int flags) {
		if ((flags & AppInfo.DOWNLOADED_FLAG) == 0) {
			// System applications cannot be installed. For now, show a toast
			// explaining that.
			// We may give them the option of disabling apps this way.
			int messageId = R.string.uninstall_system_app_text;
			Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
			return false;
		} else {
			String packageName = componentName.getPackageName();
			String className = componentName.getClassName();
			/*prize-uninstall-xiaxuefeng-2015-8-3-start*/
			if(UNINSTALL_SILENT) {
				try {
					PackageManager pm = getPackageManager();
		            PackageDeleteObserver observer = new PackageDeleteObserver();
		            pm.deletePackage(packageName, observer, 0);
				} catch (Exception e) {
					e.printStackTrace();
					Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts(
							"package", packageName, className));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					startActivity(intent);
				}
			} else {
				Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts(
						"package", packageName, className));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity(intent);
			}
			/*prize-uninstall-xiaxuefeng-2015-8-3-end*/
			return true;
		}
	}
	/*prize-uninstall-xiaxuefeng-2015-8-3-start*/
	private class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        public void packageDeleted(String packageName, int returnCode) {
            Message msg = mHandler.obtainMessage(UNINSTALL_COMPLETE);
            msg.arg1 = returnCode;
            mHandler.sendMessage(msg);           
        }    
    }
	/*prize-uninstall-xiaxuefeng-2015-8-3-end*/
	/**
	 * M: Start application uninstall activity.
	 */
	void startApplicationUninstallActivity(AppInfo appInfo) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "startApplicationUninstallActivity: appInfo = "
					+ appInfo);
		}

		if ((appInfo.flags & AppInfo.DOWNLOADED_FLAG) == 0) {
			// System applications cannot be installed. For now, show a toast
			// explaining that.
			// We may give them the option of disabling apps this way.
			int messageId = R.string.uninstall_system_app_text;
			Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
		} else {
			String packageName = appInfo.componentName.getPackageName();
			String className = appInfo.componentName.getClassName();
			Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts(
					"package", packageName, className));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
		}
	}

	// add by zhouerlong
	/**
	 * M: Start application uninstall activity.
	 */
	void startApplicationUninstallActivity(ShortcutInfo appInfo) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "startApplicationUninstallActivity: appInfo = "
					+ appInfo);
		}

		if ((appInfo.flags & AppInfo.DOWNLOADED_FLAG) == 0) {
			// System applications cannot be installed. For now, show a toast
			// explaining that.
			// We may give them the option of disabling apps this way.
			int messageId = R.string.uninstall_system_app_text;
			Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
		} else {
			String packageName = appInfo.getIntent().getComponent()
					.getPackageName();
			String className = appInfo.getIntent().getComponent()
					.getClassName();
			Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts(
					"package", packageName, className));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
		}
	}

	 private void centerAboutIcon(int [] from,int[] to ,View child) {
		 Rect target = new Rect();
		 DragLayer  parent = (DragLayer) findViewById(R.id.drag_layer);
		float scale = parent.getDescendantRectRelativeToSelf(
				child, target);// 这个就是读取folderIcon
		int width = child.getWidth();
		int height = child.getHeight();
		int bmpWidth = Utilities.sIconWidth;
		int bmpHeight = Utilities.sIconWidth;

		 int[] centerPoint = new int[2];

        float scale1 = getDragLayer().getLocationInDragLayer(child, centerPoint);
        
        int dragLayerX =
                Math.round(centerPoint[0] - (bmpWidth - scale1 * child.getWidth()) / 2);
        int dragLayerY =
                Math.round(centerPoint[1] - (bmpHeight - scale1 * child.getHeight()) / 2);

        int centerX = (int) (target.left + target.width() * scale / 2);
        int centerY = (int) (target.top + target.height() * scale / 2);
        int Left = centerX - width / 2;//差值
        int Top = centerY - height / 2;//差值
        from[0] = Left;
        from[1] = Top;

	 }
	
	// add by zhouerlong
	boolean startActivity(View v, Intent intent, Object tag) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "startActivity v = " + v + ", intent = "
					+ intent + ", tag = " + tag);
		}

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		try {
			// Only launch using the new animation if the shortcut has not opted
			// out (this is a
			// private contract between launcher and may be ignored in the
			// future).
			boolean useLaunchAnimation = (v != null)
					&& !intent.hasExtra(INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION);

			// / M: add systrace to analyze application launche time.
			Trace.traceBegin(Trace.TRACE_TAG_INPUT, "Launcher.startActivity");

			if (useLaunchAnimation) {
				int opt[] = new int[2];
				int bmpWidth = Utilities.sIconTextureHeight;
				int bmpHeight = Utilities.sIconTextureHeight;

		        float scale = getDragLayer().getLocationInDragLayer(v, opt);
				  int dragLayerX =
	                Math.round(opt[0] - (bmpWidth - scale * v.getWidth()) / 2);
	        int dragLayerY =
	                Math.round(opt[1])+v.getPaddingTop();
	        opt[0]=dragLayerX;
	        opt[1]=dragLayerY;
				ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(
						opt, v.getContext().getPackageName(), 0, 0,
						bmpWidth, bmpHeight, 600);
				startActivity(intent, opts.toBundle());
			} else {
				startActivity(intent);
			}

			// / M: add systrace to analyze application launche time.
			Trace.traceEnd(Trace.TRACE_TAG_INPUT);

			return true;
		} catch (SecurityException e) {
			Toast.makeText(this, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG,
					"Launcher does not have the permission to launch "
							+ intent
							+ ". Make sure to create a MAIN intent-filter for the corresponding activity "
							+ "or use the exported attribute for this activity. "
							+ "tag=" + tag + " intent=" + intent, e);
		}
		return false;
	}

	
	public boolean isCanSetup(Intent intent) {
		 //add by zhouerlong
        if (intent.getComponent()!= null &&intent.getComponent().toString().contains("CameraLauncher")) {
        	if (mIsLowBattery) {
        		showToast(R.string.low_battery_for_finish);
        		return false;
        	}
         }
        if (intent.getComponent()!= null &&intent.getComponent().toString().contains("LightActivity")) {
        	if (mIsLowBattery) {
        		showToast(R.string.low_battery_for_finish);
        		return false;
        	}
         }
        
        return true;
		 //add by zhouerlong
	}
	public boolean startActivitySafely(View v, Intent intent, Object tag) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "startActivitySafely v = " + v + ", intent = "
					+ intent + ", tag = " + tag);
		}

        //add by zhouerlong
		boolean isCanSetup = isCanSetup(intent);
		if (!isCanSetup) {
			return isCanSetup;
		}
		 //add by zhouerlong
		boolean success = false;
		try {
			success = startActivity(v, intent, tag);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
		}
		return success;
	}

	/**
	 * M: Start Activity For Result Safely.
	 */
	void startActivityForResultSafely(Intent intent, int requestCode) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "startActivityForResultSafely: intent = "
					+ intent + ", requestCode = " + requestCode);
		}

		try {
			startActivityForResult(intent, requestCode);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			Toast.makeText(this, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG,
					"Launcher does not have the permission to launch "
							+ intent
							+ ". Make sure to create a MAIN intent-filter for the corresponding activity "
							+ "or use the exported attribute for this activity.",
					e);
		}
	}

	public void handleFolderClick(FolderIcon folderIcon) { 
		final FolderInfo info = folderIcon.getFolderInfo();
		Folder openFolder = mWorkspace.getFolderForTag(info);

		// If the folder info reports that the associated folder is open, then
		// verify that
		// it is actually opened. There have been a few instances where this
		// gets out of sync.
		if (info.opened && openFolder == null) {
			Log.d(TAG,
					"Folder info marked as open, but associated folder is not open. Screen: "
							+ info.screenId + " (" + info.cellX + ", "
							+ info.cellY + ")");
			info.opened = false;
		}

		if (!info.opened && !folderIcon.getFolder().isDestroyed()) {
			// Close any open folder
			closeFolder();
			// Open the requested folder
			openFolder(folderIcon);
		} else {
			// Find the open folder...
			int folderScreen;
			if (openFolder != null) {
				folderScreen = mWorkspace.getPageForView(openFolder);
				// .. and close it
				closeFolder(openFolder);
				if (folderScreen != mWorkspace.getCurrentPage()) {
					// Close any folder open on the current screen
					closeFolder();
					// Pull the folder onto this screen
					openFolder(folderIcon);
				}
			}
		}
	}

	
	public void openHideApps(Rect r) {
		addHideView(mHideView);
		mHideView.setupContentForNumItems(mHideView
				.getContents().size());
		int x = r.left+r.width()/2;
		int y = r.bottom +r.height()/2;
		mHideView.animateOpen(x,y);

         mRect = new Rect();
        mDragLayer.getDescendantRectRelativeToSelf(getHotseat(),mRect);
	}
	public Rect getmRect() {
		return mRect;
	}
	public void setmRect(Rect mRect) {
		this.mRect = mRect;
	}
	/**
	 * This method draws the FolderIcon to an ImageView and then adds and
	 * positions that ImageView in the DragLayer in the exact absolute location
	 * of the original FolderIcon.
	 */
	private void copyFolderIconToImage(FolderIcon fi) {
		final int width = fi.getMeasuredWidth();
		final int height = fi.getMeasuredHeight();

		// Lazy load ImageView, Bitmap and Canvas
		if (mFolderIconImageView == null) {
			mFolderIconImageView = new ImageView(this);
		}
		if (mFolderIconBitmap == null || mFolderIconBitmap.getWidth() != width
				|| mFolderIconBitmap.getHeight() != height) {
			mFolderIconBitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			mFolderIconCanvas = new Canvas(mFolderIconBitmap);
		}

		DragLayer.LayoutParams lp;
		if (mFolderIconImageView.getLayoutParams() instanceof DragLayer.LayoutParams) {
			lp = (DragLayer.LayoutParams) mFolderIconImageView
					.getLayoutParams();
		} else {
			lp = new DragLayer.LayoutParams(width, height);
		}

		// The layout from which the folder is being opened may be scaled,
		// adjust the starting
		// view size by this scale factor.
		float scale = mDragLayer.getDescendantRectRelativeToSelf(fi,
				mRectForFolderAnimation);
		lp.customPosition = true;
		lp.x = mRectForFolderAnimation.left;
		lp.y = mRectForFolderAnimation.top;
		lp.width = (int) (scale * width);
		lp.height = (int) (scale * height);

		mFolderIconCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		fi.draw(mFolderIconCanvas);
		mFolderIconImageView.setImageBitmap(mFolderIconBitmap);
		if (fi.getFolder() != null) {
			mFolderIconImageView.setPivotX(fi.getFolder()
					.getPivotXForIconAnimation());
			mFolderIconImageView.setPivotY(fi.getFolder()
					.getPivotYForIconAnimation());
		}
		// Just in case this image view is still in the drag layer from a
		// previous animation,
		// we remove it and re-add it.
		if (mDragLayer.indexOfChild(mFolderIconImageView) != -1) {
			mDragLayer.removeView(mFolderIconImageView);
		}
		mDragLayer.addView(mFolderIconImageView, lp);
		if (fi.getFolder() != null) {
			fi.getFolder().bringToFront();
		}
	}

	private void growAndFadeOutFolderIcon(FolderIcon fi) {
		if (fi == null)
			return;
		PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0);
		PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",
				1.5f);
		PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",
				1.5f);

		FolderInfo info = (FolderInfo) fi.getTag();
		if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT
				|| info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {// M
																					// b
																					// zel
			CellLayout cl = (CellLayout) fi.getParent().getParent();
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) fi
					.getLayoutParams();
			cl.setFolderLeaveBehindCell(lp.cellX, lp.cellY);
		}

		// Push an ImageView copy of the FolderIcon into the DragLayer and hide
		// the original
		// copyFolderIconToImage(fi); //D by zhouerlong
		fi.setVisibility(View.INVISIBLE);

		ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(
				mFolderIconImageView, alpha, scaleX, scaleY);
		oa.setDuration(getResources().getInteger(
				R.integer.config_folderAnimDuration));
		// oa.start(); //D by zhouerlong
	}

	private void shrinkAndFadeInFolderIcon(final FolderIcon fi) {
		if (fi == null)
			return;
		PropertyValuesHolder alpha = PropertyValuesHolder
				.ofFloat("alpha", 1.0f);
		PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",
				1.0f);
		PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",
				1.0f);

		final CellLayout cl = (CellLayout) fi.getParent().getParent();

		// We remove and re-draw the FolderIcon in-case it has changed
		mDragLayer.removeView(mFolderIconImageView);
		// copyFolderIconToImage(fi); //D by zhouerlong
		ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(
				mFolderIconImageView, alpha, scaleX, scaleY);
		oa.setDuration(getResources().getInteger(
				R.integer.config_folderAnimDuration));
		oa.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (cl != null) {
					cl.clearFolderLeaveBehind();
					// Remove the ImageView copy of the FolderIcon and make the
					// original visible.
					mDragLayer.removeView(mFolderIconImageView);
					fi.setVisibility(View.VISIBLE);
				}
			}
		});
		oa.start();
	}
	//文件夹编辑控件
	public void addHideView(View hideapps) {
		LayoutParams lphide = new DragLayer.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lphide.customPosition = true;

		hideapps.setLayoutParams(lphide);
		if (mDragLayer.indexOfChild(hideapps) == -1) {
			mDragLayer.addView(hideapps);
		}

	}
	
	public void addVisibleView(View visibleView) {

		LayoutParams lpvisble = new DragLayer.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lpvisble.customPosition = true;

		visibleView.setLayoutParams(lpvisble);
		if (mDragLayer.indexOfChild(visibleView) == -1) {
			mDragLayer.addView(visibleView);
		}
		visibleView.bringToFront();
	}

	/**
	 * Opens the user folder described by the specified tag. The opening of the
	 * folder is animated relative to the specified View. If the View is null,
	 * no animation is played.
	 * 
	 * @param folderInfo
	 *            The FolderInfo describing the folder to open.
	 */
	public void openFolder(FolderIcon folderIcon) {
		Folder folder = folderIcon.getFolder();

		// Just verify that the folder hasn't already been added to the
		// DragLayer.
		// There was a one-off crash where the folder had a parent already.
		if (folder.getParent() == null) {
			mDragLayer.addView(folder);
			mDragController.addDropTarget((DropTarget) folder);
		} else {
			Log.w(TAG, "Opening folder (" + folder
					+ ") which already has a parent (" + folder.getParent()
					+ ").");
		}
	        	folder.setBackgroundResource(0); 
		folder.animateOpen();
		growAndFadeOutFolderIcon(folderIcon);

		// Notify the accessibility manager that this folder "window" has
		// appeared and occluded
		// the workspace items
		folder.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
		getDragLayer().sendAccessibilityEvent(
				AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
	}

	public void closeFolder() {
		Folder folder = mWorkspace.getOpenFolder();
		if (folder != null) {
			if (folder.isEditingName()) {
				folder.dismissEditingName();
			}
			closeFolder(folder);

			// Dismiss the folder cling
			dismissFolderCling(null);
		}
		
		HideAppView hideView = getHideView();
		if (hideView !=null) {
			hideView.animateClosed();	
		}
		
	}
	
	public void onTranslationNotContainDeleteTarget() {

		/*if(mWorkspace.getVisibility() != View.VISIBLE) {
			mWorkspace.setVisibility(View.VISIBLE);
		}
		if(mOverviewPanel.getVisibility() != View.VISIBLE) {
			mOverviewPanel.setVisibility(View.VISIBLE);
		}*/
	}
	

	public void onTranslationContainDeleteTarget() {

		/*if(mWorkspace.getVisibility() != View.GONE) {
			mWorkspace.setVisibility(View.GONE);
		}
		if(mOverviewPanel.getVisibility() != View.GONE) {
			mOverviewPanel.setVisibility(View.GONE);
		}*/
	}
	private float mProgress =0;
	float mTempProgress=0;
	private int mHeight =0;
	public void setTranslationYByProgress(float  progress,float translationY,int height) {
//		mLauncherBackground.bringToFront();
		if (mLauncherBackground.getVisibility() == View.GONE) {
			mLauncherBackground.setVisibility(View.VISIBLE);
		}
		if (mWallpaperBg.getVisibility() == View.GONE) {
			mWallpaperBg.setVisibility(View.VISIBLE);
		}
		if(mWorkspace.getVisibility() != View.GONE) {
			mWorkspace.setVisibility(View.GONE);
		}
		if(mOverviewPanel.getVisibility() != View.GONE) {
			mOverviewPanel.setVisibility(View.GONE);
		}

        View dockView = getworkspace().isInSpringLoadMoed()?getOverviewPanel():getHotseat();
      /*  getworkspace().setTranslationY(translationY);
        dockView.setTranslationY(translationY);
        mPageIndicators.setTranslationY(translationY);*/
        mLauncherBackground.setTranslationY(translationY);
        mLauncherBackground.setprogress(progress);
        mWallpaperBg.setAlpha(progress);
        mProgress = mTempProgress = progress;
        this.mHeight = height;
        LogUtils.i("zhouerlong", "setTranslationYByProgress:;;;;;progress--"+progress);
        if (mProgress ==0f) {
          mLauncherBackground.setAlpha(0);
          mWallpaperBg.setAlpha(0);
          LogUtils.i("zhouerlong", "setTranslationYByProgress:;;;;;progress--"+progress);
        }
        mLauncherBackground.invalidate();
        mWallpaperBg.invalidate();
	}
	
	
	public void setTranslationYByTouch(float  progress,float translationY,int height) {

        View dockView = getworkspace().isInSpringLoadMoed()?getOverviewPanel():getHotseat();
        mHideAppsView.setTranslationY(translationY-mHideAppsView.getHeight());
        dockView.setTranslationY(translationY);
        if(mHideAppsView.getVisibility()== View.GONE) {
        	mHideAppsView.setVisibility(View.VISIBLE);
        	mHideAppsView.bringToFront();
        }
        translationY = translationY-translationY/3;
        mWorkspace.setTranslationY(translationY);
        mWorkspace.getPageIndicator().setTranslationY(translationY);
        mWorkspace.setScaleX((1-progress*0.1f));
        mWorkspace.setScaleY((1-progress*0.1f));
        mHideAppsView.getChildAt(0).setScaleX((1-progress*0.1f));
        mHideAppsView.getChildAt(0).setScaleY((1-progress*0.1f));
	}
	
	public void onCloseComplete() {

		onTranslationContainDeleteTarget();
		mProgress =0f;
		mTempProgress =0f;
		mLauncherBackground.setLauncherBg(null, null);
		mLauncherBackground.invalidate();
		
	}
	public void updateAlphaByExplosionView(float progress,boolean inOrout) {
		if (!inOrout) {
	        mLauncherBackground.setprogress(progress);
	        mWallpaperBg.setAlpha(progress);
		}else {
			mProgress =progress+mTempProgress;
			mLauncherBackground.setprogress(mProgress);
	        mWallpaperBg.setAlpha(mProgress);
		}
        mLauncherBackground.invalidate();
        mWallpaperBg.invalidate();
	}
	
	public void updateTranslationYbyExplosionView(float progress,
			boolean inOrOut) {
        View dockView = getworkspace().isInSpringLoadMoed()?getOverviewPanel():getHotseat();
		int translationY;
		if (inOrOut) {
			int h = (int) (mTempProgress>0?mTempProgress*mHeight:mHeight*progress);
			 translationY = (int) (progress
					* mExplosionDialogView.getMeasuredHeight() / 2 + h );
			 

		}else {
			int h = (int) (mTempProgress>0?mTempProgress*mHeight:mHeight*progress);
			 translationY = (int) (progress
					* (mExplosionDialogView.getMeasuredHeight() / 2 + h));
		}
		/*getworkspace().setTranslationY(translationY);
		dockView.setTranslationY(translationY);
		mPageIndicators.setTranslationY(translationY);*/
		mLauncherBackground.setTranslationY(translationY);
	}
	
	public void onUninstallActivityReturned() {
		ValueAnimator a = ValueAnimator.ofFloat(mProgress,0f);
		a.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				mLauncherBackground.setLayerType(View.LAYER_TYPE_HARDWARE, null);
				mWallpaperBg.setLayerType(View.LAYER_TYPE_HARDWARE, null);
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {

		        enterDeleteDropTarget(false);
				
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		a.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float p = (float) animation.getAnimatedValue();
		          LogUtils.i("zhouerlong", "setTranslationYByProgress:;;;;;pppppppppp--"+p);
				setTranslationYByProgress(p, (float)mHeight*p,mHeight);
				
			}
		});
		a.setDuration(500);
		a.start();
	}
	
	/**
	 * 第二种方法的延伸
	 * 
	 * @param bm
	 * @param view
	 * @return void
	 * @author Doraemon
	 * @time 2014年7月7日下午4:56:53
	 */
	private Bitmap rsBlur2(Bitmap bm, View view) {
		Bitmap outputBitmap = Bitmap.createBitmap(
				(int) (view.getMeasuredWidth() / 1),
				(int) (view.getMeasuredHeight() / 1), Bitmap.Config.ARGB_8888);

		RenderScript rs = RenderScript.create(this);
		ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs,
				Element.U8_4(rs));
		Allocation tmpIn = Allocation.createFromBitmap(rs, bm);
		Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
		theIntrinsic.setRadius(25.f);
		theIntrinsic.setInput(tmpIn);
		theIntrinsic.forEach(tmpOut);
		tmpOut.copyTo(outputBitmap);
		rs.destroy();
		return outputBitmap;

	}
	
	public void setTraslationYByBlueBg() {
				mLauncherBackground.setVisibility(View.GONE);
				mWallpaperBg.setVisibility(View.GONE);
				View p = this.findViewById(R.id.workspaceAndOther);
				
				if (false) {
					Bitmap launcherView =ImageUtils.convertViewToBitmap(p,p.getMeasuredWidth(),p.getMeasuredHeight());
					Drawable mlauncherOrg = ImageUtils.bitmapToDrawable(launcherView);
					Bitmap blurScreen = rsBlur2(launcherView, mLauncherBackground);
					Drawable[] d = new Drawable[2];
					final Drawable bac_launcher = new BitmapDrawable(blurScreen);
					d[0] = bac_launcher;
					BlueTask b = new BlueTask(this, mLauncherBackground, mWallpaperBg);
					b.execute();
					blurBackground(d,mlauncherOrg);
				}else {
					p.invalidate();
					Bitmap launcherView =ImageUtils.convertViewToBitmap(p,p.getMeasuredWidth(),p.getMeasuredHeight());
					BlueTask b = new BlueTask(this, mLauncherBackground, mWallpaperBg);
					b.execute(launcherView);
				}
	}

	
	public void blurBackground(Drawable bac_launcher[],Drawable mlauncherOrg) {
		// final Drawable bac_launcher = new
		// BitmapDrawable(blurScale(getScreenView()));
		mLauncherBackground.setLauncherBg(bac_launcher[0], mlauncherOrg);
		mWallpaperBg.setLauncherBg(bac_launcher[1], null);
		mLauncherBackground.invalidate();
		mWallpaperBg.invalidate();
		// mLauncherBackground.setAlpha(0f);
	}
	void closeFolder(Folder folder) {
//		folder.getInfo().opened = false;
		Log.i("zhouerlong", "close Folder");
		ViewGroup parent = (ViewGroup) folder.getParent().getParent();
		if (parent != null) {
			FolderIcon fi = (FolderIcon) mWorkspace.getViewForTag(folder.mInfo);
			shrinkAndFadeInFolderIcon(fi);
		}
        if (folder.getAnimationingState() == Folder.STATE_OPEN) {

            folder.animateClosed();
        }

		// Notify the accessibility manager that this folder "window" has
		// disappeard and no
		// longer occludeds the workspace items
		getDragLayer().sendAccessibilityEvent(
				AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
	}
	
	public boolean onLongClick(View v) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onLongClick: View = " + v + ", v.getTag() = "
					+ v.getTag() + ", mState = " + mState);
		}
		/*
		 * 这里是基于测试用的
		* mThemesLinearLayoutView.bindRemoveItem("com.theme.test2");

		 */
		if (!isDraggingEnabled()) {
			LauncherLog.d(TAG, "onLongClick: isDraggingEnabled() = "
					+ isDraggingEnabled());// 拖动是否激活
			return false;
		}

		if (isWorkspaceLocked()) {
			LauncherLog.d(TAG,
					"onLongClick: isWorkspaceLocked() mWorkspaceLoading "
							+ mWorkspaceLoading + ", mWaitingForResult = "
							+ mWaitingForResult);
			return false;
		}
		
		

		if (mState != State.WORKSPACE) {
			LauncherLog.d(TAG, "onLongClick: mState != State.WORKSPACE: = "
					+ mState);
			return false;
		}

		if (!(v instanceof CellLayout)) {
			v = (View) v.getParent().getParent();
		}

		resetAddInfo();
		CellLayout.CellInfo longClickCellInfo = (CellLayout.CellInfo) v
				.getTag();
		// This happens when long clicking an item with the dpad/trackball
		if (longClickCellInfo == null) {
			return true;
		}

		// The hotseat touch handling does not go through Workspace, and we
		// always allow long press
		// on hotseat items.
		final View itemUnderLongClick = longClickCellInfo.cell;
		boolean allowLongPress = isHotseatLayout(v)
				|| mWorkspace.allowLongPress();
		if (allowLongPress && !mDragController.isDragging()) {
			if (itemUnderLongClick == null) {
				// User long pressed on empty space
				mWorkspace.performHapticFeedback(
						HapticFeedbackConstants.LONG_PRESS,
						HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
				// Disabling reordering until we sort out some issues.
				// d by zhouerlong
				/*
				 * if (mWorkspace.isInOverviewMode()) {
				 * mWorkspace.startReordering(v); } else {
				 * mWorkspace.enterOverviewMode(); }
				 */
				// 注视掉这里是不让他进入 over模式

				// add by zhouerlong

				if (mWorkspace.isInSpringLoadMoed()) {
					// mWorkspace.startReordering(v);
					mWorkspace.onBackpressed();
					mWorkspace.exitSpringLoadMode(true, false);
				} else {
					mWorkspace.enterSprindLoadMode(this.getCurrrentSpringState());// 进入编辑模式
				}
				// add by zhouerlong
			} else {
				if (!(itemUnderLongClick instanceof Folder)) {
					// / M: Call the appropriate callback for the IMtkWidget on
					// the current page
					// / when long click and begin to drag IMtkWidget.
					mWorkspace.startDragAppWidget(mWorkspace.getCurrentPage());

					// User long pressed on an item
					mWorkspace.startDrag(longClickCellInfo);
				}
			}
		}
		return true;
	}

	boolean isHotseatLayout(View layout) {
		return mHotseat != null && layout != null
				&& (layout instanceof CellLayout)
				&& (layout == mHotseat.getLayout());
	}


	boolean notHotseatLayoutAndHideAppsLayout(View layout) {
		return !isHotseatLayout(layout)&&!isHideAppsLayout(layout);
	}
	
	boolean isHideAppsLayout(View layout) {
		return mHideAppsView != null && layout.getParent() != null
				&& (layout instanceof CellLayout)
				&& (layout == mHideAppsView.getLayout());
	}
	
	HideAppsView getHideAppsView() {
		return mHideAppsView;
	}

	Hotseat getHotseat() {
		return mHotseat;
	}


	SearchView getSearchView() {
		return mSearchView;
	}
	View getOverviewPanel() {
		return mOverviewPanel;
	}

	SearchDropTargetBar getSearchBar() {
		return mSearchDropTargetBar;
	}

	/**
	 * Returns the CellLayout of the specified container at the specified
	 * screen.
	 */
	CellLayout getCellLayout(long container, long screenId) {
		if (container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
			if (mHotseat != null) {
				return mHotseat.getLayout();
			} else {
				return null;
			}
		} else {
			return (CellLayout) mWorkspace.getScreenWithId(screenId);
		}
	}

	Workspace getWorkspace() {
		return mWorkspace;
	}
	public HideAppView getHideView() {
		return mHideView;
	}
	public VisibleAppsView getVisibleView() {
		return mVisibleView;
	}
	public boolean isAllAppsVisible() {
		// mo by zhouerlong
		return (mState == State.APPS_CUSTOMIZE)
				|| (mOnResumeState == State.APPS_CUSTOMIZE)
				|| (mState == State.WORKSPACE);
	}

	/**
	 * Helper method for the cameraZoomIn/cameraZoomOut animations
	 * 
	 * @param view
	 *            The view being animated
	 * @param scaleFactor
	 *            The scale factor used for the zoom
	 */
	private void setPivotsForZoom(View view, float scaleFactor) {
		view.setPivotX(view.getWidth() / 2.0f);
		view.setPivotY(view.getHeight() / 2.0f);
	}

	private void setWorkspaceBackground(boolean workspace) {
		/*
		 * mLauncherView.setBackground(workspace ? mWorkspaceBackgroundDrawable
		 * : null);
		 */
	}

	void updateWallpaperVisibility(boolean visible) {
		int wpflags = visible ? WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
				: 0;
		int curflags = getWindow().getAttributes().flags
				& WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
		if (wpflags != curflags) {
			getWindow().setFlags(wpflags,
					WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
		}
		setWorkspaceBackground(visible);
	}

	private void dispatchOnLauncherTransitionPrepare(View v, boolean animated,
			boolean toWorkspace) {
		if (v instanceof LauncherTransitionable) {
			((LauncherTransitionable) v).onLauncherTransitionPrepare(this,
					animated, toWorkspace);
		}
	}

	private void dispatchOnLauncherTransitionStart(View v, boolean animated,
			boolean toWorkspace) {
		if (v instanceof LauncherTransitionable) {
			((LauncherTransitionable) v).onLauncherTransitionStart(this,
					animated, toWorkspace);
		}

		// Update the workspace transition step as well
		dispatchOnLauncherTransitionStep(v, 0f);
	}

	private void dispatchOnLauncherTransitionStep(View v, float t) {
		if (v instanceof LauncherTransitionable) {
			((LauncherTransitionable) v).onLauncherTransitionStep(this, t);
		}
	}

	private void dispatchOnLauncherTransitionEnd(View v, boolean animated,
			boolean toWorkspace) {
		if (v instanceof LauncherTransitionable) {
			((LauncherTransitionable) v).onLauncherTransitionEnd(this,
					animated, toWorkspace);
			// mo by zhouerlong
			if (v instanceof AppsCustomizePagedView) {
				AppsCustomizePagedView appsView = (AppsCustomizePagedView) v;
				appsView.loadAssociatedPages(appsView.getCurrentPage());
			}
		}

		// Update the workspace transition step as well
		dispatchOnLauncherTransitionStep(v, 1f);
	}

	/**
	 * Things to test when changing the following seven functions. - Home from
	 * workspace - from center screen - from other screens - Home from all apps
	 * - from center screen - from other screens - Back from all apps - from
	 * center screen - from other screens - Launch app from workspace and quit -
	 * with back - with home - Launch app from all apps and quit - with back -
	 * with home - Go to a screen that's not the default, then all apps, and
	 * launch and app, and go back - with back -with home - On workspace, long
	 * press power and go back - with back - with home - On all apps, long press
	 * power and go back - with back - with home - On workspace, power off - On
	 * all apps, power off - Launch an app and turn off the screen while in that
	 * app - Go back with home key - Go back with back key TODO: make this not
	 * go to workspace - From all apps - From workspace - Enter and exit car
	 * mode (becuase it causes an extra configuration changed) - From all apps -
	 * From the center workspace - From another workspace
	 */

	/**
	 * Zoom the camera out from the workspace to reveal 'toView'. Assumes that
	 * the view to show is anchored at either the very top or very bottom of the
	 * screen.
	 */
	private void showAppsCustomizeHelper(final boolean animated,
			final boolean springLoaded) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "showAppsCustomizeHelper animated = " + animated
					+ ", springLoaded = " + springLoaded);
		}

//		AppsCustomizePagedView.ContentType contentType = mAppsCustomizeContent
//				.getContentType();
//		showAppsCustomizeHelper(animated, springLoaded, contentType,
//				EditState.NORMAL);// mo by zhouerlong
	}

	View toView;// add by zhouerlong
	private List<Animator> mAnimationList;
		//add by zel

	// mo by zhouerlong
	private void showAppsCustomizeHelper(final boolean animated,
			final boolean springLoaded,
			final AppsCustomizePagedView.ContentType contentType,
			EditState state) {
		if (mStateAnimation != null) {
			mStateAnimation.setDuration(0);
			mStateAnimation.cancel();
			mStateAnimation = null;
		}
		final Resources res = getResources();

		final int fadeDuration = res
				.getInteger(R.integer.config_appsCustomizeFadeInTime);// 淡入
		final float scale = (float) res
				.getInteger(R.integer.config_appsCustomizeZoomScaleFactor); // 比例
		final View fromView = mWorkspace;// 隐藏的view
		toView = this.mWorkspace; // 显示的view
		if (state == EditState.SPRING_LOAD) {
			toView = mAppsCustomizeContentSpringWidget;
			// M by zhouerlong
		}
		final int startDelay = res
				.getInteger(R.integer.config_workspaceAppsCustomizeAnimationStagger);

		setPivotsForZoom(toView, scale);// 设置动画效果中心点
		
		// Shrink workspaces away if going to AppsCustomize from workspace
		Animator workspaceAnim;
		if (state == EditState.SPRING_LOAD) {
			workspaceAnim = mWorkspace.getChangeStateAnimation(
					Workspace.State.SPRING_LOADED, animated);// 读取动画装体值
		} else {
			workspaceAnim = mWorkspace.getChangeStateAnimation(
					Workspace.State.SMALL, animated);// 读取动画装体值
			// A by zel
		}
		if (!AppsCustomizePagedView.DISABLE_ALL_APPS) {
			// Set the content type for the all apps space
			// mAppsCustomizeTabHost.setContentTypeImmediate(contentType);
			// M by zhouerlong
			/*
			 * this.mAppsCustomizeContentSpringWidget.setContentType(contentType)
			 * ;
			 * this.mAppsCustomizeContentSpringApps.setContentType(contentType);
			 */
			// D b zel
		}

		if (animated) {
		//add by zel
			if(mAnimationList!=null)
			mAnimationList.clear();
			final AbsAppsAnimation anim = this.getChangeAppsAnimation(); //modify by zhouerlong
			mAnimationList = anim.getAnimators(fadeDuration * 3, new DecelerateInterpolator(1.5f), toView,false);
			anim.setPreAnimationData(toView);
			/*final LauncherViewPropertyAnimator scaleAnim = new LauncherViewPropertyAnimator(
					toView);
			scaleAnim.scaleX(1f).scaleY(1f).// 缩放大小
					setDuration(fadeDuration * 5).// 时间
					setInterpolator(new Workspace.ZoomOutInterpolator());// 速度变化
*/			// toView 的动画 设置

			toView.setVisibility(View.VISIBLE);
			toView.setAlpha(0f);
			final ObjectAnimator alphaAnim = LauncherAnimUtils.ofFloat(toView,
					"alpha", 0f, 1f).setDuration(fadeDuration * 3);
			/*final ObjectAnimator rotationAnim = LauncherAnimUtils.ofFloat(
					toView, "rotation", 0f, 360f).setDuration(fadeDuration * 5);*/
			alphaAnim.setInterpolator(mZoomInInterpolator);
			alphaAnim.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					if (animation == null) {
						throw new RuntimeException("animation is null");
					}
					float t = (Float) animation.getAnimatedValue();
					dispatchOnLauncherTransitionStep(fromView, t);
					dispatchOnLauncherTransitionStep(toView, t);
				}
			});

			// toView should appear right at the end of the workspace shrink
			// animation
			mStateAnimation = LauncherAnimUtils.createAnimatorSet();
				for(int i=0;i<mAnimationList.size();i++) {
					mStateAnimation.play(mAnimationList.get(i)).after(startDelay);
				}
//			mStateAnimation.play(scaleAnim).after(startDelay);
			// mStateAnimation.play(rotationAnim).after(startDelay);
		//add by zel
			mStateAnimation.play(alphaAnim).after(startDelay);

			mStateAnimation.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					// Prepare the position
					toView.setTranslationX(0.0f);
					toView.setTranslationY(0.0f);
					toView.setVisibility(View.VISIBLE);
					toView.bringToFront();
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					dispatchOnLauncherTransitionEnd(fromView, animated, false);
					dispatchOnLauncherTransitionEnd(toView, animated, false);
					
					// Hide the search bar
					if (mSearchDropTargetBar != null) {
						anim.OnAnimationEnd(toView);
						//modify by zhouerlong
						mSearchDropTargetBar.hideSearchBar(false);
					}
				}
			});

			if (workspaceAnim != null) {
				// 不播放动画
				mStateAnimation.play(workspaceAnim);
				// A by zel
			}

			boolean delayAnim = false;

			dispatchOnLauncherTransitionPrepare(fromView, animated, false);
			dispatchOnLauncherTransitionPrepare(toView, animated, false);

			// If any of the objects being animated haven't been measured/laid
			// out
			// yet, delay the animation until we get a layout pass
			if ((((LauncherTransitionable) toView).getContent()
					.getMeasuredWidth() == 0)
					|| (mWorkspace.getMeasuredWidth() == 0)
					|| (toView.getMeasuredWidth() == 0)) {
				delayAnim = true;
			}

			final AnimatorSet stateAnimation = mStateAnimation;
			final Runnable startAnimRunnable = new Runnable() {
				public void run() {
					// Check that mStateAnimation hasn't changed while
					// we waited for a layout/draw pass
					if (mStateAnimation != stateAnimation)
						return;
					setPivotsForZoom(toView, scale);
					dispatchOnLauncherTransitionStart(fromView, animated, false);
					dispatchOnLauncherTransitionStart(toView, animated, false);
					LauncherAnimUtils.startAnimationAfterNextDraw(
							mStateAnimation, toView);
				}
			};
			if (delayAnim) {
				final ViewTreeObserver observer = toView.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						startAnimRunnable.run();
						toView.getViewTreeObserver()
								.removeOnGlobalLayoutListener(this);
					}
				});
			} else {
				startAnimRunnable.run();
			}
		} else {
			toView.setTranslationX(0.0f);
			toView.setTranslationY(0.0f);
			toView.setScaleX(1.0f);
			toView.setScaleY(1.0f);
			toView.setVisibility(View.VISIBLE);
			toView.bringToFront();

			if (!springLoaded
					&& !LauncherAppState.getInstance().isScreenLarge()) {
				// Hide the search bar
				if (mSearchDropTargetBar != null) {
					mSearchDropTargetBar.hideSearchBar(false);
				}
			}
			dispatchOnLauncherTransitionPrepare(fromView, animated, false);
			dispatchOnLauncherTransitionStart(fromView, animated, false);
			dispatchOnLauncherTransitionEnd(fromView, animated, false);
			dispatchOnLauncherTransitionPrepare(toView, animated, false);
			dispatchOnLauncherTransitionStart(toView, animated, false);
			dispatchOnLauncherTransitionEnd(toView, animated, false);
		}
	}

	/**
	 * Zoom the camera back into the workspace, hiding 'fromView'. This is the
	 * opposite of showAppsCustomizeHelper.
	 * 
	 * @param animated
	 *            If true, the transition will be animated.
	 *            隐藏AppsCustomizePageView 显示workspace
	 */
	private void hideAppsCustomizeHelper(Workspace.State toState,
			final boolean animated, final boolean springLoaded,
			final Runnable onCompleteRunnable) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "hideAppsCustomzieHelper toState = " + toState
					+ ", animated = " + animated + ", springLoaded = "
					+ springLoaded);
		}

		if (mStateAnimation != null) {
			mStateAnimation.setDuration(0);
			mStateAnimation.cancel();
			mStateAnimation = null;
		}
		Resources res = getResources();

		final int duration = res
				.getInteger(R.integer.config_appsCustomizeZoomOutTime);
		final int fadeOutDuration = res
				.getInteger(R.integer.config_appsCustomizeFadeOutTime);
		final float scaleFactor = (float) res
				.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
		final View fromView = mWorkspace;
		final View toView = mWorkspace;
		Animator workspaceAnim = null;
		if (toState == Workspace.State.NORMAL) {
			int stagger = res
					.getInteger(R.integer.config_appsCustomizeWorkspaceAnimationStagger);
			workspaceAnim = mWorkspace.getChangeStateAnimation(toState,
					animated, stagger, -1);
		} else if (toState == Workspace.State.SPRING_LOADED
				|| toState == Workspace.State.OVERVIEW) {
			workspaceAnim = mWorkspace.getChangeStateAnimation(toState,
					animated);
		}

		setPivotsForZoom(fromView, scaleFactor);
		showHotseat(animated);
		if (animated) {
			final LauncherViewPropertyAnimator scaleAnim = new LauncherViewPropertyAnimator(
					fromView);
			scaleAnim.scaleX(scaleFactor).scaleY(scaleFactor)
					.setDuration(duration)
					.setInterpolator(new Workspace.ZoomInInterpolator());

			final ObjectAnimator alphaAnim = LauncherAnimUtils.ofFloat(
					fromView, "alpha", 1f, 0f).setDuration(fadeOutDuration);

		//add by zel
			final ObjectAnimator rotationAnim = LauncherAnimUtils.ofFloat(
					toView, "rotationY", 360f, 0f).setDuration(fadeOutDuration);
			rotationAnim
					.setInterpolator(new AccelerateDecelerateInterpolator());
			rotationAnim.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float t = 1f - (Float) animation.getAnimatedValue();
					dispatchOnLauncherTransitionStep(fromView, t);
					dispatchOnLauncherTransitionStep(toView, t);
				}
			});

			mStateAnimation = LauncherAnimUtils.createAnimatorSet();

			dispatchOnLauncherTransitionPrepare(fromView, animated, true);
			dispatchOnLauncherTransitionPrepare(toView, animated, true);
//			mAppsCustomizeContent.pauseScrolling();

			mStateAnimation.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					fromView.setVisibility(View.GONE);
					dispatchOnLauncherTransitionEnd(fromView, animated, true);
					dispatchOnLauncherTransitionEnd(toView, animated, true);
					if (onCompleteRunnable != null) {
						onCompleteRunnable.run();
					}
//					mAppsCustomizeContent.updateCurrentPageScroll();
//					mAppsCustomizeContent.resumeScrolling();
					LauncherLog.d(TAG,
							"[PerfTest --> drag widget] end process.");
				}
			});

		//add by zel
			mStateAnimation.playTogether(rotationAnim, alphaAnim);
			if (workspaceAnim != null) {
				mStateAnimation.play(workspaceAnim);
			}
			dispatchOnLauncherTransitionStart(fromView, animated, true);
			dispatchOnLauncherTransitionStart(toView, animated, true);
			LauncherAnimUtils.startAnimationAfterNextDraw(mStateAnimation,
					toView);
		} else {
			fromView.setVisibility(View.GONE);
			dispatchOnLauncherTransitionPrepare(fromView, animated, true);
			dispatchOnLauncherTransitionStart(fromView, animated, true);
			dispatchOnLauncherTransitionEnd(fromView, animated, true);
			dispatchOnLauncherTransitionPrepare(toView, animated, true);
			dispatchOnLauncherTransitionStart(toView, animated, true);
			dispatchOnLauncherTransitionEnd(toView, animated, true);
		}
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "onTrimMemory: level = " + level);
		}

		/*if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
			mAppsCustomizeTabHost.onTrimMemory();
		}*/
	}

	protected void showWorkspace(boolean animated) {
		showWorkspace(animated, null);
	}

	protected void showWorkspace() {
		showWorkspace(true);
	}

	void showWorkspace(boolean animated, Runnable onCompleteRunnable) {
		Trace.traceBegin(Trace.TRACE_TAG_INPUT, "showWorkspace");
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "showWorkspace: animated = " + animated
					+ ", mState = " + mState);
		}

		// / M: Call the appropriate callback for the IMtkWidget on the current
		// page when leave all apps list back to
		// / workspace.
		mWorkspace.stopCovered(mWorkspace.getCurrentPage());

		if (mWorkspace.isInOverviewMode()) {
			mWorkspace.exitOverviewMode(animated);
		}
		// add by zhouerlong
		if (mWorkspace.isInSpringLoadMoed()) {
			mWorkspace.exitSpringLoadMode(animated,false);

		}
		// mo by zhouerlong
		// 不做判断
		// add by zhouerlong
		if (mState != State.WORKSPACE) {
			boolean wasInSpringLoadedMode = (mState != State.WORKSPACE);
			mWorkspace.setVisibility(View.VISIBLE);
			hideAppsCustomizeHelper(Workspace.State.NORMAL, animated, false,
					onCompleteRunnable);

			// Show the search bar (only animate if we were showing the drop
			// target bar in spring
			// loaded mode)
			if (mSearchDropTargetBar != null) {
				mSearchDropTargetBar.showSearchBar(animated
						&& wasInSpringLoadedMode);
			}

			// Set focus to the AppsCustomize button
			if (mAllAppsButton != null) {
				mAllAppsButton.requestFocus();
			}
		}

		// Change the state *after* we've called all the transition code
		mState = State.WORKSPACE;

		// Resume the auto-advance of widgets
		mUserPresent = true;
		updateRunning();

		// Send an accessibility event to announce the context change
		getWindow().getDecorView().sendAccessibilityEvent(
				AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);

		onWorkspaceShown(animated);
		Trace.traceEnd(Trace.TRACE_TAG_INPUT);
	}

	void showOverviewMode(boolean animated) {
		mWorkspace.setVisibility(View.VISIBLE);
		hideAppsCustomizeHelper(Workspace.State.OVERVIEW, animated, false, null);
		mState = State.WORKSPACE;
		onWorkspaceShown(animated);
	}

	// m by zhouerlong
	void showSpringLoadModel(boolean animated) {
		mWorkspace.setVisibility(View.VISIBLE);
		hideAppsCustomizeHelper(Workspace.State.SPRING_LOADED, animated, false,
				null);
		mState = State.WORKSPACE;
		onWorkspaceShown(animated);
	}

	public void onWorkspaceShown(boolean animated) {
	}

	// A by zel

	/*
	 * public Context mSystemUIContext; public Object mPhoneStatusBar; public
	 * void testCloseDragHandle() { try { mSystemUIContext =
	 * this.createPackageContext("com.android.systemui",
	 * Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY); Class
	 * cls = mSystemUIContext.getClassLoader().loadClass(
	 * "com.android.systemui.statusbar.phone.PhoneStatusBar"); mPhoneStatusBar =
	 * cls.newInstance(); Log.v(TAG,
	 * "++++++++++ Start testCloseDragHandle ++++++++++"); Object commandQueue =
	 * TestUtils.getSuperClassProperty(mPhoneStatusBar, "mCommandQueue");
	 * 
	 * animateExpandNotificationsPanel TestUtils.invokeMethod(commandQueue,
	 * "animateExpandSettingsPanel", null, null); TestUtils.sleepBy(1000);
	 * TestUtils.invokeMethod(commandQueue, "animateExpandSettingsPanel", null,
	 * null); TestUtils.sleepBy(1000); TestUtils.invokeMethod(commandQueue,
	 * "animateCollapsePanels", null, null); TestUtils.sleepBy(1000); Log.v(TAG,
	 * "---------- end testCloseDragHandle ----------"); }catch (Exception e){
	 * e.printStackTrace(); } }
	 */

	public void collapse() {
		try {

			Object service = this.getSystemService("statusbar");
			Class<?> statusBarManager = Class
					.forName("android.app.StatusBarManager");
			Method expand = statusBarManager.getMethod("collapsePanels");
			expand.invoke(service);
			Log.d("click", "-------------2");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {

		}
	}

	public void expand() {
		if(mDragLayer.indexOfChild(mHideView)!=-1) {
			return;
		}
		try {
			Object service = this.getSystemService("statusbar");
			Class<?> statusBarManager = Class
					.forName("android.app.StatusBarManager");
			Method expand = statusBarManager
					.getMethod("expandNotificationsPanel");
			expand.invoke(service);
			Log.d("click", "-------------2");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// A by zel
	void showAllApps(boolean animated,
			AppsCustomizePagedView.ContentType contentType,
			boolean resetPageToZero) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "showAllApps: animated = " + animated
					+ ", mState = " + mState + ", mCurrentBounds = "
					+ mCurrentBounds);
		}
		if (mState != State.WORKSPACE)
			return;

		// / M: Call the appropriate callback for the IMtkWidget on the current
		// page when enter all apps list.
		mWorkspace.startCovered(mWorkspace.getCurrentPage());// 开始覆盖
		// ?????????????
		
		/* if (resetPageToZero) {
			 mAppsCustomizeTabHost.reset(); 
		 }*/
		 
		// mo by zhouerlong
		showAppsCustomizeHelper(animated, false, contentType, EditState.NORMAL);
		// M: tab container may be GONE due to new UI design, so we change focus
		// to mAppsCustomizeContent
	/*	if (mAppsCustomizeTabHost.isTabContainerVisible() == false) {
//			mAppsCustomizeContent.requestFocus();
		} else {
			mAppsCustomizeTabHost.requestFocus();
		}*/

		// Change the state *after* we've called all the transition code
		mState = State.APPS_CUSTOMIZE;

		// Pause the auto-advance of widgets until we are out of AllApps
		mUserPresent = false;
		updateRunning();
		closeFolder();

		// Send an accessibility event to announce the context change
		getWindow().getDecorView().sendAccessibilityEvent(
				AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
	}

	// add by zhouerlong
	void showAllAppsWidget(boolean animated,
			AppsCustomizePagedView.ContentType contentType,
			boolean resetPageToZero) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "showAllApps: animated = " + animated
					+ ", mState = " + mState + ", mCurrentBounds = "
					+ mCurrentBounds);
		}
		if (mState != State.WORKSPACE)
			return;

		// / M: Call the appropriate callback for the IMtkWidget on the current
		// page when enter all apps list.
		mWorkspace.startCovered(mWorkspace.getCurrentPage());// 开始覆盖

		if (resetPageToZero) {
//			mAppsCustomizeTabHost.reset();// 不需要
		}
		showAppsCustomizeHelper(animated, false, contentType,
				EditState.SPRING_LOAD);
		// M: tab container may be GONE due to new UI design, so we change focus
		// to mAppsCustomizeContent
		/*if (mAppsCustomizeTabHost.isTabContainerVisible() == false) {
//			mAppsCustomizeContent.requestFocus();
		} else {
			mAppsCustomizeTabHost.requestFocus();
		}*/

		// Change the state *after* we've called all the transition code
		mState = State.APPS_CUSTOMIZE;

		// Pause the auto-advance of widgets until we are out of AllApps
		mUserPresent = false;
		updateRunning();
		closeFolder();

		// Send an accessibility event to announce the context change
		getWindow().getDecorView().sendAccessibilityEvent(
				AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
	}

	void enterWorkspaceDragMode() {
		hideAppsCustomizeHelper(Workspace.State.NORMAL, true, true, null);
		mState = State.WORKSPACE;

	}

	void enterSpringLoadedDragMode() {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "enterSpringLoadedDragMode mState = " + mState
					+ ", mOnResumeState = " + mOnResumeState);
		}
		// m by zhouerlong
		if (isAllAppsVisible()) {
			// hideAppsCustomizeHelper(Workspace.State.SPRING_LOADED, true,
			// true, null);
			mState = State.WORKSPACE;
			// M by zhouerlong
		}
	}

	void exitSpringLoadedDragModeDelayed(final boolean successfulDrop,
			boolean extendedDelay, final Runnable onCompleteRunnable) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG,
					"exitSpringLoadedDragModeDelayed successfulDrop = "
							+ successfulDrop + ", extendedDelay = "
							+ extendedDelay + ", mState = " + mState);
		}

		if (mState != State.APPS_CUSTOMIZE_SPRING_LOADED)
			return;

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (successfulDrop) {
					// Before we show workspace, hide all apps again because
					// exitSpringLoadedDragMode made it visible. This is a bit
					// hacky; we should
					// clean up our state transition functions
//					mAppsCustomizeTabHost.setVisibility(View.GONE);
					showWorkspace(true, onCompleteRunnable);
				} else {
					exitSpringLoadedDragMode(false);
				}
			}
		}, (extendedDelay ? EXIT_SPRINGLOADED_MODE_LONG_TIMEOUT
				: EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT));
	}

	void exitSpringLoadedDragMode(boolean isHomeDown) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "exitSpringLoadedDragMode mState = " + mState);
		}
		if (mState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
			mState = State.APPS_CUSTOMIZE;
		}
		if (getworkspace().isInSpringLoadMoed()) {
			if (!isHomeDown)
			mMulEditNagiration.revert(false);
		}
		// Otherwise, we are not in spring loaded mode, so don't do anything.
	}

	void lockAllApps() {
		// TODO
	}

	void unlockAllApps() {
		// TODO
	}

	/**
	 * Shows the hotseat area.
	 */
	void showHotseat(boolean animated) {
		if (!LauncherAppState.getInstance().isScreenLarge()) {
			if (animated) {
				if (mHotseat.getAlpha() != 1f) {
					int duration = 0;
					if (mSearchDropTargetBar != null) {
						duration = mSearchDropTargetBar
								.getTransitionInDuration();
					}
					mHotseat.animate().alpha(1f).setDuration(duration);
				}
			} else {
				mHotseat.setAlpha(1f);
			}
		}
	}

	/**
	 * Hides the hotseat area.
	 */
	void hideHotseat(boolean animated) {
		if (!LauncherAppState.getInstance().isScreenLarge()) {
			if (animated) {
				if (mHotseat.getAlpha() != 0f) {
					int duration = 0;
					if (mSearchDropTargetBar != null) {
						duration = mSearchDropTargetBar
								.getTransitionOutDuration();
					}
					mHotseat.animate().alpha(0f).setDuration(duration);
				}
			} else {
				mHotseat.setAlpha(0f);
			}
		}
	}

	/**
	 * Add an item from all apps or customize onto the given workspace screen.
	 * If layout is null, add to the current screen.
	 */
	void addExternalItemToScreen(ItemInfo itemInfo, final CellLayout layout,int duration,View view) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "addExternalItemToScreen itemInfo = " + itemInfo
					+ ", layout = " + layout);
		}

		if (!mWorkspace.addExternalItemToScreen(itemInfo, layout,duration,view)) {
//			showOutOfSpaceMessage(isHotseatLayout(layout));
		}
	}
	
	void addExternalItemToScreen(ItemInfo itemInfo,int duration,CellLayout targetLayout,View v) {

		if (mWorkspace.isEmptyScreenIdForCurrentScreen(targetLayout)) {//add by zel 如果是无效页 将新建新的页面

			mWorkspace.commitExtraEmptyScreen();
			mWorkspace.addExtraEmptyScreenOnDrag();
			
		}
		targetLayout = this.getWorkspace().getCurrentDropLayout();
		
		addExternalItemToScreen(itemInfo,targetLayout, duration,v);
	}
	
	
	void addExternalShortItemsToScreen(ItemInfo infoInfo,View view) {
		CellLayout targetLayout = this.getWorkspace().getCurrentDropLayout();
		addExternalShortItemsToScreen(infoInfo,targetLayout,view);
	}
	
	

	void addExternalShortItemsToScreen(ItemInfo itemInfo,
			CellLayout targetLayout, View v) {
		if (itemInfo instanceof FolderInfo) {
			int i = 0;
			int startDelay = this.getResources().getInteger(
					R.integer.config_appsCustomizeFadeInTime);
			FolderInfo folderInfo = (FolderInfo) itemInfo;
			for (ShortcutInfo info : folderInfo.getContents()) {

				addExternalItemToScreen(info, i * startDelay, targetLayout, v);
				i++;
			}
			Log.i("ss", "mama");
		} else {// add by zhouerlong 添加app 自动加入桌面
			addExternalItemToScreen(itemInfo, 0, targetLayout, v);// add by
																	// zhouerlong添加widget动画
		}
	}
	
	public void setFolderDecompression(boolean decompression) {
		folderDecompression = decompression;
	}

	public boolean getFolderDecompression() {
		return false;//folderDecompression;
	}

	/**
	 * Maps the current orientation to an index for referencing orientation
	 * correct global icons
	 */
	private int getCurrentOrientationIndexForGlobalIcons() {
		// default - 0, landscape - 1
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			return 1;
		default:
			return 0;
		}
	}

	private Drawable getExternalPackageToolbarIcon(ComponentName activityName,
			String resourceName) {
		try {
			PackageManager packageManager = getPackageManager();
			// Look for the toolbar icon specified in the activity meta-data
			Bundle metaData = packageManager.getActivityInfo(activityName,
					PackageManager.GET_META_DATA).metaData;
			if (metaData != null) {
				int iconResId = metaData.getInt(resourceName);
				if (iconResId != 0) {
					Resources res = packageManager
							.getResourcesForActivity(activityName);
					return res.getDrawable(iconResId);
				}
			}
		} catch (NameNotFoundException e) {
			// This can happen if the activity defines an invalid drawable
			Log.w(TAG,
					"Failed to load toolbar icon; "
							+ activityName.flattenToShortString()
							+ " not found", e);
		} catch (Resources.NotFoundException nfe) {
			// This can happen if the activity defines an invalid drawable
			Log.w(TAG,
					"Failed to load toolbar icon from "
							+ activityName.flattenToShortString(), nfe);
		}
		return null;
	}
	// if successful in getting icon, return it; otherwise, set button to use
	// default drawable
	private Drawable.ConstantState updateTextButtonWithIconFromExternalActivity(
			int buttonId, ComponentName activityName, int fallbackDrawableId,
			String toolbarResourceName) {
		Drawable toolbarIcon = getExternalPackageToolbarIcon(activityName,
				toolbarResourceName);
		Resources r = getResources();
		int w = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_width);
		int h = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_height);

		TextView button = (TextView) findViewById(buttonId);
		// If we were unable to find the icon via the meta-data, use a generic
		// one
		if (toolbarIcon == null) {
			toolbarIcon = getDrawable(fallbackDrawableId);
			toolbarIcon.setBounds(0, 0, w, h);
			if (button != null) {
				button.setCompoundDrawables(toolbarIcon, null, null, null);
			}
			return null;
		} else {
			toolbarIcon.setBounds(0, 0, w, h);
			if (button != null) {
				button.setCompoundDrawables(toolbarIcon, null, null, null);
			}
			return toolbarIcon.getConstantState();
		}
	}

	// if successful in getting icon, return it; otherwise, set button to use
	// default drawable
	private Drawable.ConstantState updateButtonWithIconFromExternalActivity(
			int buttonId, ComponentName activityName, int fallbackDrawableId,
			String toolbarResourceName) {
		ImageView button = (ImageView) findViewById(buttonId);
		Drawable toolbarIcon = getExternalPackageToolbarIcon(activityName,
				toolbarResourceName);

		if (button != null) {
			// If we were unable to find the icon via the meta-data, use a
			// generic one
			if (toolbarIcon == null) {
				button.setImageResource(fallbackDrawableId);
			} else {
				button.setImageDrawable(toolbarIcon);
			}
		}

		return toolbarIcon != null ? toolbarIcon.getConstantState() : null;

	}

	private void updateTextButtonWithDrawable(int buttonId, Drawable d) {
		TextView button = (TextView) findViewById(buttonId);
		button.setCompoundDrawables(d, null, null, null);
	}

	private void updateButtonWithDrawable(int buttonId, Drawable.ConstantState d) {
		ImageView button = (ImageView) findViewById(buttonId);
		button.setImageDrawable(d.newDrawable(getResources()));
	}

	private void invalidatePressedFocusedStates(View container, View button) {
		if (container instanceof HolographicLinearLayout) {
			HolographicLinearLayout layout = (HolographicLinearLayout) container;
			layout.invalidatePressedFocusedStates();
		} else if (button instanceof HolographicImageView) {
			HolographicImageView view = (HolographicImageView) button;
			view.invalidatePressedFocusedStates();
		}
	}

	public View getQsbBar() {
		if (mQsbBar == null) {
			mQsbBar = mInflater.inflate(R.layout.search_bar,
					mSearchDropTargetBar, false);
			mSearchDropTargetBar.addView(mQsbBar);
		}
		return mQsbBar;
	}

	protected boolean updateGlobalSearchIcon() {
		final View searchButtonContainer = findViewById(R.id.search_button_container);
		final ImageView searchButton = (ImageView) findViewById(R.id.search_button);
		final View voiceButtonContainer = findViewById(R.id.voice_button_container);
		final View voiceButton = findViewById(R.id.voice_button);

		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		ComponentName activityName = searchManager.getGlobalSearchActivity();
		if (activityName != null) {
			// / M: only show search engine name on non-OP01 projects.
			final boolean needUpdate = false;/*LauncherExtPlugin
					.getInstance()
					.getSearchButtonExt(this)
					.needUpdateSearchButtonResource(searchButton,
							R.drawable.ic_home_search_normal_holo);*/
			if (LauncherLog.DEBUG) {
				LauncherLog.d(TAG, "updateGlobalSearchIcon: needUpdate = "
						+ needUpdate + ",activityName = " + activityName);
			}
			if (needUpdate) {
				int coi = getCurrentOrientationIndexForGlobalIcons();
				sGlobalSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
						R.id.search_button, activityName,
						R.drawable.ic_home_search_normal_holo,
						TOOLBAR_SEARCH_ICON_METADATA_NAME);
				if (sGlobalSearchIcon[coi] == null) {
					sGlobalSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
							R.id.search_button, activityName,
							R.drawable.ic_home_search_normal_holo,
							TOOLBAR_ICON_METADATA_NAME);
				}
			}

			if (searchButtonContainer != null)
				searchButtonContainer.setVisibility(View.VISIBLE);
			searchButton.setVisibility(View.VISIBLE);
			invalidatePressedFocusedStates(searchButtonContainer, searchButton);
			return true;
		} else {
			// We disable both search and voice search when there is no global
			// search provider
			if (searchButtonContainer != null)
				searchButtonContainer.setVisibility(View.GONE);
			if (voiceButtonContainer != null)
				voiceButtonContainer.setVisibility(View.GONE);
			if (searchButton != null)
				searchButton.setVisibility(View.GONE);
			if (voiceButton != null)
				voiceButton.setVisibility(View.GONE);
			// / M: [ALPS01257663] Correct usage of
			// updateVoiceButtonProxyVisible().
			updateVoiceButtonProxyVisible(true);
			return false;
		}
	}

	protected void updateGlobalSearchIcon(Drawable.ConstantState d) {
		final View searchButtonContainer = findViewById(R.id.search_button_container);
		final View searchButton = (ImageView) findViewById(R.id.search_button);
		updateButtonWithDrawable(R.id.search_button, d);
		invalidatePressedFocusedStates(searchButtonContainer, searchButton);
	}

	protected boolean updateVoiceSearchIcon(boolean searchVisible) {
		final View voiceButtonContainer = findViewById(R.id.voice_button_container);
		final View voiceButton = findViewById(R.id.voice_button);

		// We only show/update the voice search icon if the search icon is
		// enabled as well
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		ComponentName globalSearchActivity = searchManager
				.getGlobalSearchActivity();

		ComponentName activityName = null;
		if (globalSearchActivity != null) {
			// Check if the global search activity handles voice search
			Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
			intent.setPackage(globalSearchActivity.getPackageName());
			activityName = intent.resolveActivity(getPackageManager());
		}

		if (activityName == null) {
			// Fallback: check if an activity other than the global search
			// activity
			// resolves this
			Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
			activityName = intent.resolveActivity(getPackageManager());
		}
		if (searchVisible && activityName != null) {
			int coi = getCurrentOrientationIndexForGlobalIcons();
			sVoiceSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
					R.id.voice_button, activityName,
					R.drawable.ic_home_voice_search_holo,
					TOOLBAR_VOICE_SEARCH_ICON_METADATA_NAME);
			if (sVoiceSearchIcon[coi] == null) {
				sVoiceSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
						R.id.voice_button, activityName,
						R.drawable.ic_home_voice_search_holo,
						TOOLBAR_ICON_METADATA_NAME);
			}
			if (voiceButtonContainer != null)
				voiceButtonContainer.setVisibility(View.GONE);
			voiceButton.setVisibility(View.GONE);
			updateVoiceButtonProxyVisible(false);
			invalidatePressedFocusedStates(voiceButtonContainer, voiceButton);
			return true;
		} else {
			if (voiceButtonContainer != null)
				voiceButtonContainer.setVisibility(View.GONE);
			if (voiceButton != null)
				voiceButton.setVisibility(View.GONE);
			// / M: [ALPS01257663] Correct usage of
			// updateVoiceButtonProxyVisible().
			updateVoiceButtonProxyVisible(true);
			return false;
		}
	}

	protected void updateVoiceSearchIcon(Drawable.ConstantState d) {
		final View voiceButtonContainer = findViewById(R.id.voice_button_container);
		final View voiceButton = findViewById(R.id.voice_button);
		updateButtonWithDrawable(R.id.voice_button, d);
		invalidatePressedFocusedStates(voiceButtonContainer, voiceButton);
	}

	public void updateVoiceButtonProxyVisible(
			boolean forceDisableVoiceButtonProxy) {
		final View voiceButtonProxy = findViewById(R.id.voice_button_proxy);
		if (voiceButtonProxy != null) {
			boolean visible = !forceDisableVoiceButtonProxy
					&& mWorkspace.shouldVoiceButtonProxyBeVisible();
			voiceButtonProxy.setVisibility(visible ? View.GONE : View.GONE);
			voiceButtonProxy.bringToFront();
		}
	}

	/**
	 * This is an overrid eot disable the voice button proxy. If disabled is
	 * true, then the voice button proxy will be hidden regardless of what
	 * shouldVoiceButtonProxyBeVisible() returns.
	 */
	public void disableVoiceButtonProxy(boolean disabled) {
		updateVoiceButtonProxyVisible(disabled);
	}

	/**
	 * Sets the app market icon
	 */
	private void updateAppMarketIcon() {
		if (!DISABLE_MARKET_BUTTON) {
			final View marketButton = findViewById(R.id.market_button);
			Intent intent = new Intent(Intent.ACTION_MAIN)
					.addCategory(Intent.CATEGORY_APP_MARKET);
			// Find the app market activity by resolving an intent.
			// (If multiple app markets are installed, it will return the
			// ResolverActivity.)
			ComponentName activityName = intent
					.resolveActivity(getPackageManager());
			if (activityName != null) {
				int coi = getCurrentOrientationIndexForGlobalIcons();
				mAppMarketIntent = intent;
				sAppMarketIcon[coi] = updateTextButtonWithIconFromExternalActivity(
						R.id.market_button, activityName,
						R.drawable.ic_launcher_market_holo,
						TOOLBAR_ICON_METADATA_NAME);
				marketButton.setVisibility(View.VISIBLE);
			} else {
				// We should hide and disable the view so that we don't try and
				// restore the visibility
				// of it when we swap between drag & normal states from
				// IconDropTarget subclasses.
				marketButton.setVisibility(View.GONE);
				marketButton.setEnabled(false);
			}
		}
	}

	private void updateAppMarketIcon(Drawable.ConstantState d) {
		if (!DISABLE_MARKET_BUTTON) {
			// Ensure that the new drawable we are creating has the approprate
			// toolbar icon bounds
			Resources r = getResources();
			Drawable marketIconDrawable = d.newDrawable(r);
			int w = r
					.getDimensionPixelSize(R.dimen.toolbar_external_icon_width);
			int h = r
					.getDimensionPixelSize(R.dimen.toolbar_external_icon_height);
			marketIconDrawable.setBounds(0, 0, w, h);

			updateTextButtonWithDrawable(R.id.market_button, marketIconDrawable);
		}
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		final boolean result = super.dispatchPopulateAccessibilityEvent(event);
		final List<CharSequence> text = event.getText();
		text.clear();
		// Populate event with a fake title based on the current state.
		if (mState == State.APPS_CUSTOMIZE) {
			/*text.add(mAppsCustomizeTabHost.getCurrentTabView()
					.getContentDescription());*/
		} else {
			text.add(getString(R.string.all_apps_home_button_label));
		}
		return result;
	}

	/**
	 * Receives notifications when system dialogs are to be closed.
	 */
	private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (LauncherLog.DEBUG) {
				LauncherLog.d(TAG, "Close system dialogs: intent = " + intent);
			}
			closeSystemDialogs();
		}
	}

	
	
	
	
	/**
	 * Receives notifications whenever the appwidgets are reset.
	 */
	private class AppWidgetResetObserver extends ContentObserver {
		public AppWidgetResetObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			onAppWidgetReset();
		}
	}

	/**
	 * If the activity is currently paused, signal that we need to run the
	 * passed Runnable in onResume.
	 * 
	 * This needs to be called from incoming places where resources might have
	 * been loaded while we are paused. That is becaues the Configuration might
	 * be wrong when we're not running, and if it comes back to what it was when
	 * we were paused, we are not restarted.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 * 
	 * @return true if we are currently paused. The caller might be able to skip
	 *         some work in that case since we will come back again.
	 */
	private boolean waitUntilResume(Runnable run,
			boolean deletePreviousRunnables) {
		if (mPaused) {
			Log.i(TAG, "Deferring update until onResume");
			if (deletePreviousRunnables) {
				while (mBindOnResumeCallbacks.remove(run)) {
				}
			}
			mBindOnResumeCallbacks.add(run);
			return true;
		} else {
			return false;
		}
	}

	private boolean waitUntilResume(Runnable run) {
		return waitUntilResume(run, false);
	}

	public void addOnResumeCallback(Runnable run) {
		mOnResumeCallbacks.add(run);
	}

	/**
	 * If the activity is currently paused, signal that we need to re-run the
	 * loader in onResume.
	 * 
	 * This needs to be called from incoming places where resources might have
	 * been loaded while we are paused. That is becaues the Configuration might
	 * be wrong when we're not running, and if it comes back to what it was when
	 * we were paused, we are not restarted.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 * 
	 * @return true if we are currently paused. The caller might be able to skip
	 *         some work in that case since we will come back again.
	 */
	public boolean setLoadOnResume() {
		if (mPaused) {
			LauncherLog.i(TAG, "setLoadOnResume: this = " + this);
			mOnResumeNeedsLoad = true;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public int getCurrentWorkspaceScreen() {
		if (mWorkspace != null) {
			return mWorkspace.getCurrentPage();
		} else {
			return SCREEN_COUNT / 2;
		}
	}

	/**
	 * Refreshes the shortcuts shown on the workspace.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void startBinding() {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "startBinding: this = " + this);
		}

		// If we're starting binding all over again, clear any bind calls we'd
		// postponed in
		// the past (see waitUntilResume) -- we don't need them since we're
		// starting binding
		// from scratch again
		mBindOnResumeCallbacks.clear();

		// Clear the workspace because it's going to be rebound
		mWorkspace.clearDropTargets();
		mWorkspace.removeAllWorkspaceScreens();

		mWidgetsToAdvance.clear();
		if (mHotseat != null) {
			mHotseat.resetLayout();
		}
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "startBinding: mIsLoadingWorkspace = "
					+ mIsLoadingWorkspace);
		}
	}

	@Override
	public void bindScreens(ArrayList<Long> orderedScreenIds) {
		bindAddScreens(orderedScreenIds);

		// If there are no screens, we need to have an empty screen
		if (orderedScreenIds.size() == 0) {
			mWorkspace.addExtraEmptyScreen();
		}

		// Create the custom content page (this call updates mDefaultScreen
		// which calls
		// setCurrentPage() so ensure that all pages are added before calling
		// this).
		// The actual content of the custom page will be added during
		// onFinishBindingItems().
		if (!mWorkspace.hasCustomContent() && hasCustomContentToLeft()) {
			mWorkspace.createCustomContentPage();
		}
	}

	@Override
	public void bindAddScreens(ArrayList<Long> orderedScreenIds) {
		int count = orderedScreenIds.size();
		for (int i = 0; i < count; i++) {
			mWorkspace
					.insertNewWorkspaceScreenBeforeEmptyScreen(orderedScreenIds
							.get(i));
		}
	}

	private boolean shouldShowWeightWatcher() {
		String spKey = LauncherAppState.getSharedPreferencesKey();
		SharedPreferences sp = getSharedPreferences(spKey, Context.MODE_PRIVATE);
		boolean show = sp.getBoolean(SHOW_WEIGHT_WATCHER,
				SHOW_WEIGHT_WATCHER_DEFAULT);

		return show;
	}

	private void toggleShowWeightWatcher() {
		String spKey = LauncherAppState.getSharedPreferencesKey();
		SharedPreferences sp = getSharedPreferences(spKey, Context.MODE_PRIVATE);
		boolean show = sp.getBoolean(SHOW_WEIGHT_WATCHER, true);

		show = !show;

		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(SHOW_WEIGHT_WATCHER, show);
		editor.commit();

		if (mWeightWatcher != null) {
			mWeightWatcher.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	public void bindAppsAdded(final ArrayList<Long> newScreens,
			final ArrayList<ItemInfo> addNotAnimated,
			final ArrayList<ItemInfo> addAnimated,
			final ArrayList<AppInfo> addedApps) {
		Runnable r = new Runnable() {
			public void run() {
				bindAppsAdded(newScreens, addNotAnimated, addAnimated,
						addedApps);
			}
		};
		/*if (waitUntilResume(r)) {
			return;
		}*/
		// Add the new screens
		bindAddScreens(newScreens);

		// We add the items without animation on non-visible pages, and with
		// animations on the new page (which we will try and snap to).
		if (!addNotAnimated.isEmpty()) {
			bindItems(addNotAnimated, 0, addNotAnimated.size(), false);
		}
		if (!addAnimated.isEmpty()) {
			bindItems(addAnimated, 0, addAnimated.size(), true);
		}

		// Remove the extra empty screen
		mWorkspace.removeExtraEmptyScreen();
		// M by zhouerlong
		/*if (!AppsCustomizePagedView.DISABLE_ALL_APPS && addedApps != null
				&& mAppsCustomizeContent != null
				&& this.mAppsCustomizeContentSpringWidget != null) {
			// / M: delay to handle apps added event if app list is in edit
			// mode, for op09.
			if (mSupportEditAndHideApps
					&& (sUsePendingAppsQueue || !mBindingAppsFinished || !mAppsCustomizeContent
							.isDataReady())
					|| !this.mAppsCustomizeContentSpringWidget.isDataReady()) {
				if (LauncherLog.DEBUG) {
//					LauncherLog.d(TAG, "bindAppsAdded: sUsePendingAppsQueue = "
//							+ sUsePendingAppsQueue
//							+ ", mBindingAppsFinished = "
//							+ mBindingAppsFinished
//							+ ", mAppsCustomizeContent.isDataReady() = "
////							+ mAppsCustomizeContent.isDataReady());
				}
				sPendingChangedApps.add(new PendingChangedApplications(
						addedApps, PendingChangedApplications.TYPE_ADDED));
			} else {
//				mAppsCustomizeContent.addApps(addedApps);
//				this.mAppsCustomizeContentSpringWidget.addApps(addedApps);
			}
*//** M: If unread information load completed, we need to update information in app list.@{**//*
        if (mUnreadLoadCompleted) {
            AppsCustomizePagedView.updateUnreadNumInAppInfo(addedApps);
        }
        *//**@}**//*
		}*/
	}
	
	
	public void bindItem(ShortcutInfo shortcut, final boolean forceAnimateIcons) {
		View shortcutView = createShortcut(shortcut);
		final boolean animateIcons = forceAnimateIcons
				&& canRunNewAppsAnimation();
		Workspace workspace = mWorkspace;
		long newShortcutsScreenId = -1;
		final Collection<Animator> bounceAnims = new ArrayList<Animator>();
		if (shortcut.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT
				&& mHotseat == null) {
			return;
		}

		workspace.addInScreenFromBind(shortcutView, shortcut.container,
				shortcut.screenId, shortcut.cellX, shortcut.cellY, 1, 1);
		if (animateIcons) {
			// Animate all the applications up now
			shortcutView.setAlpha(0f);
			shortcutView.setScaleX(1f);
			shortcutView.setScaleY(1f);
			bounceAnims.add(createNewAppBounceAnimation(shortcutView, 0));
			newShortcutsScreenId = shortcut.screenId;
		}

		if (animateIcons) {
			// Animate to the correct page
			if (newShortcutsScreenId > -1) {
				long currentScreenId = mWorkspace
						.getScreenIdForPageIndex(mWorkspace.getNextPage());
				final int newScreenIndex = mWorkspace
						.getPageIndexForScreenId(newShortcutsScreenId);
				final Runnable startBounceAnimRunnable = new Runnable() {
					public void run() {
						anim.playTogether(bounceAnims);
						anim.start();
					}
				};
				if (newShortcutsScreenId != currentScreenId) {
					// We post the animation slightly delayed to prevent
					// slowdowns
					// when we are loading right after we return to launcher.
					mWorkspace.postDelayed(new Runnable() {
						public void run() {
							mWorkspace.snapToPage(newScreenIndex);
							mWorkspace.postDelayed(startBounceAnimRunnable,
									NEW_APPS_ANIMATION_DELAY);
						}
					}, NEW_APPS_PAGE_MOVE_DELAY);
				} else {
					mWorkspace.postDelayed(startBounceAnimRunnable,
							NEW_APPS_ANIMATION_DELAY);
				}
			}
		}

	}
	
	public void bindHideApps() {
		
		mHideView.setAllApps(hides);
		mVisibleView.setAllApps(visibles);
	}
	
	private boolean isDowanLoad(AppInfo shortcutInfo) {
		return (shortcutInfo.flags & AppInfo.DOWNLOADED_FLAG) != 0;
	}
	
	private boolean isDowanLoad(ShortcutInfo shortcutInfo) {
		return (shortcutInfo.flags & AppInfo.DOWNLOADED_FLAG) != 0;
	}

	// M by zhouerlong
	/**
	 * Bind the items start-end from the list.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void bindItems(final ArrayList<ItemInfo> shortcuts, final int start,
			final int end, final boolean forceAnimateIcons) {
		Runnable r = new Runnable() {
			public void run() {
				bindItems(shortcuts, start, end, forceAnimateIcons);
			}
		};
		/*if (waitUntilResume(r)) {
			return;
		}*/

		// Get the list of added shortcuts and intersect them with the set of
		// shortcuts here
		final AnimatorSet anim = LauncherAnimUtils.createAnimatorSet();
		final Collection<Animator> bounceAnims = new ArrayList<Animator>();
		final boolean animateIcons = forceAnimateIcons
				&& canRunNewAppsAnimation();
		Workspace workspace = mWorkspace;
		long newShortcutsScreenId = -1;
		for (int i = start; i < end; i++) {
			final ItemInfo item = shortcuts.get(i);
			if (LauncherLog.DEBUG) {
				LauncherLog.d(TAG, "bindItems: start = " + start + ", end = "
						+ end + "item = " + item + ", this = " + this);
			}

			// Short circuit if we are loading dock items for a configuration
			// which has no dock
			if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT
					&& mHotseat == null) {
				continue;
			}

			switch (item.itemType) {
			case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
			case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
				ShortcutInfo info = (ShortcutInfo) item;
				View shortcut = createShortcut(info);
				

				if(isSupportHideApp) {
				if(isDowanLoad(info)) {
					if(info.hide==1) {
						hides.add(info);
					}else {
						visibles.add(info);
						
					}
				}
				if(info.hide ==1) {
					continue;
				}
				}
				/*
				 * TODO: FIX collision case
				 */
				if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
					CellLayout cl = mWorkspace.getScreenWithId(item.screenId);
					/*if (cl != null && cl.isOccupied(item.cellX, item.cellY)) {
						throw new RuntimeException("OCCUPIED");
					}*/
				}
				workspace.addInScreenFromBind(shortcut, item.container,
						item.screenId, item.cellX, item.cellY, 1, 1);
				if (animateIcons) {
					// Animate all the applications up now
					shortcut.setAlpha(0f);
					shortcut.setScaleX(0f);
					shortcut.setScaleY(0f);
					bounceAnims.add(createNewAppBounceAnimation(shortcut, i));
					newShortcutsScreenId = item.screenId;
				}
				break;
			case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
				FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon,
						this, (ViewGroup) workspace.getChildAt(workspace
								.getCurrentPage()), (FolderInfo) item,
						mIconCache);
				workspace.addInScreenFromBind(newFolder, item.container,
						item.screenId, item.cellX, item.cellY, 1, 1);
				break;
			default:
				throw new RuntimeException("Invalid Item Type");
			}
		}
		mBindingWorkspaceFinished=true;
		if (animateIcons) {
			// Animate to the correct page
			if (newShortcutsScreenId > -1) {
				long currentScreenId = mWorkspace
						.getScreenIdForPageIndex(mWorkspace.getNextPage());
				final int newScreenIndex = mWorkspace
						.getPageIndexForScreenId(newShortcutsScreenId);
				final Runnable startBounceAnimRunnable = new Runnable() {
					public void run() {
						anim.playTogether(bounceAnims);
						anim.start();
					}
				};
				if (newShortcutsScreenId != currentScreenId) {
					// We post the animation slightly delayed to prevent
					// slowdowns
					// when we are loading right after we return to launcher.
					mWorkspace.postDelayed(new Runnable() {
						public void run() {
							mWorkspace.snapToPage(newScreenIndex);
							mWorkspace.postDelayed(startBounceAnimRunnable,
									NEW_APPS_ANIMATION_DELAY);
						}
					}, NEW_APPS_PAGE_MOVE_DELAY);
				} else {
					mWorkspace.postDelayed(startBounceAnimRunnable,
							NEW_APPS_ANIMATION_DELAY);
				}
			}
		}
		workspace.requestLayout();
	}

	/**
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void bindFolders(final HashMap<Long, FolderInfo> folders) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "bindFolders: this = " + this);
		}

		Runnable r = new Runnable() {
			public void run() {
				bindFolders(folders);
			}
		};
		if (waitUntilResume(r)) {
			return;
		}
		sFolders.clear();
		sFolders.putAll(folders);
		mModel.OnFolderItemsChange();// A by zhouerlong
	}

	/**
	 * Add the views for a widget to the workspace.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void bindAppWidget(final LauncherAppWidgetInfo item) {
		Runnable r = new Runnable() {
			public void run() {
				bindAppWidget(item);
			}
		};
		if (waitUntilResume(r)) {
			return;
		}

		final long start = DEBUG_WIDGETS ? SystemClock.uptimeMillis() : 0;
		if (DEBUG_WIDGETS) {
			Log.d(TAG, "bindAppWidget: " + item);
		}
		final Workspace workspace = mWorkspace;

		final int appWidgetId = item.appWidgetId;
		final AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager
				.getAppWidgetInfo(appWidgetId);
		if (DEBUG_WIDGETS) {
			Log.d(TAG, "bindAppWidget: id=" + item.appWidgetId
					+ " belongs to component "
					+ (appWidgetInfo == null ? "" : appWidgetInfo.provider));
		}

		item.hostView = mAppWidgetHost.createView(this, appWidgetId,
				appWidgetInfo);
		if (item.hostView instanceof LauncherAppWidgetHostView) {
			LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) item.hostView;
	//add by zhouerlong 20150808 begin
			hostView.setObservable(mNifObserables);
			hostView.registerObserver();
	//add by zhouerlong 20150808 end
			hostView.setOnClickListener(this);
		}
		item.hostView.setTag(item);
		item.onBindAppWidget(this);

		// / M: Call the appropriate callback for the IMtkWidget will be bound
		// to workspace.
		mWorkspace.setAppWidgetIdAndScreen(item.hostView,
				mWorkspace.getCurrentPage(), appWidgetId);

		workspace.addInScreen(item.hostView, item.container, item.screenId,
				item.cellX, item.cellY, item.spanX, item.spanY, false);
		addWidgetToAutoAdvanceIfNeeded(item.hostView, appWidgetInfo);

		workspace.requestLayout();

		if (DEBUG_WIDGETS) {
			Log.d(TAG, "bound widget id=" + item.appWidgetId + " in "
					+ (SystemClock.uptimeMillis() - start) + "ms");
		}
	}

	public void onPageBoundSynchronously(int page) {
		mSynchronouslyBoundPages.add(page);
	}

	/**
	 * Callback saying that there aren't any more items to bind.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void finishBindingItems(final boolean upgradePath) { if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "finishBindingItems: mSavedState = "
					+ mSavedState + ", mSavedInstanceState = "
					+ mSavedInstanceState + ", this = " + this);
		}

		Runnable r = new Runnable() {
			public void run() {
				finishBindingItems(upgradePath);
			}
		};
		if (waitUntilResume(r)) {
			return;
		}
		if (mSavedState != null) {
			if (!mWorkspace.hasFocus()) {
				mWorkspace.getChildAt(mWorkspace.getCurrentPage())
						.requestFocus();
			}
			mSavedState = null;
		}

		mWorkspace.restoreInstanceStateForRemainingPages();

		// If we received the result of any pending adds while the loader was
		// running (e.g. the
		// widget configuration forced an orientation change), process them now.
		for (int i = 0; i < sPendingAddList.size(); i++) {
			completeAdd(sPendingAddList.get(i));
		}
		sPendingAddList.clear();

		// Update the market app icon as necessary (the other icons will be
		// managed in response to
		// package changes in bindSearchablesChanged()
		if (!DISABLE_MARKET_BUTTON) {
			updateAppMarketIcon();
		}
 /** M: If unread information load completed, we need to bind it to workspace.@{**/
        if (mUnreadLoadCompleted) {
            bindWorkspaceUnreadInfo();
        }
		mWorkspaceLoading = false;
		if (upgradePath) {
			mWorkspace.getUniqueComponents(true, null);
			mIntentsOnWorkspaceFromUpgradePath = mWorkspace
					.getUniqueComponents(true, null);
		}

		mWorkspace.post(new Runnable() {
			@Override
			public void run() {
				onFinishBindingItems();
			}
		});
	}

	public boolean isAllAppsButtonRank(int rank) {
		if (mHotseat != null) {
			return mHotseat.isAllAppsButtonRank(rank);
		}
		return false;
	}

	private boolean canRunNewAppsAnimation() {
		long diff = System.currentTimeMillis()
				- mDragController.getLastGestureUpTime();
		return diff > (NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS * 1000);
	}

	private ValueAnimator createNewAppBounceAnimation(View v, int i) {
		ValueAnimator bounceAnim = LauncherAnimUtils.ofPropertyValuesHolder(v,
				PropertyValuesHolder.ofFloat("alpha", 1f),
				PropertyValuesHolder.ofFloat("scaleX", 1f),
				PropertyValuesHolder.ofFloat("scaleY", 1f));
		bounceAnim
				.setDuration(InstallShortcutReceiver.NEW_SHORTCUT_BOUNCE_DURATION);
		bounceAnim.setStartDelay(i
				* InstallShortcutReceiver.NEW_SHORTCUT_STAGGER_DELAY);
		bounceAnim.setInterpolator(new SmoothPagedView.OvershootInterpolator());
		return bounceAnim;
	}

	@Override
	public void bindSearchablesChanged() {
		boolean searchVisible = updateGlobalSearchIcon();
		boolean voiceVisible = updateVoiceSearchIcon(searchVisible);
		if (mSearchDropTargetBar != null) {
			mSearchDropTargetBar.onSearchPackagesChanged(searchVisible,
					voiceVisible);
		}
	}

	/**
	 * Add the icons for all apps.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void bindAllApplications(final ArrayList<AppInfo> apps) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "bindAllApplications: apps = " + apps);
		}

		if (AppsCustomizePagedView.DISABLE_ALL_APPS) {
			if (mIntentsOnWorkspaceFromUpgradePath != null) {
				if (LauncherModel.UPGRADE_USE_MORE_APPS_FOLDER) {
					getHotseat().addAllAppsFolder(mIconCache, apps,
							mIntentsOnWorkspaceFromUpgradePath, Launcher.this,
							mWorkspace);
				}
				mIntentsOnWorkspaceFromUpgradePath = null;

			}
			if (AppsCustomizePagedView.DISABLE_ALL_APPS) {
				mAppsCustomizeContentSpringWidget.setApps(apps);// add by
			}

			if(isSupportHideApp) {
				bindHideApps();
				mHideView.bindAllApps(apps);
				mVisibleView.bindAllApps(apps);
			}

			
			
		} 
	}

	/**
	 * A package was updated.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void bindAppsUpdated(final ArrayList<AppInfo> apps,final boolean fromappStore) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "bindAppsUpdated: apps = " + apps);
		}

		Runnable r = new Runnable() {
			public void run() {
				bindAppsUpdated(apps,fromappStore);
			}
		};
		if (waitUntilResume(r)) {
			return;
		}

		if (mWorkspace != null) {

			if(fromappStore) {
				mWorkspace.updateShortcutsFromAppStore(apps);
			}else {
				mWorkspace.updateShortcuts(apps);
			}
		}

		/*if (!AppsCustomizePagedView.DISABLE_ALL_APPS
				&& mAppsCustomizeContent != null) {
			// / M: delay to handle apps update event if app list is in edit
			// mode, for op09.
			if (mSupportEditAndHideApps
					&& (sUsePendingAppsQueue || !mBindingAppsFinished || !mAppsCustomizeContent
							.isDataReady())) {
				if (LauncherLog.DEBUG) {
					LauncherLog
							.d(TAG,
									"bindAppsUpdated: sUsePendingAppsQueue = "
											+ sUsePendingAppsQueue
											+ ", mBindingAppsFinished = "
											+ mBindingAppsFinished
											+ ", mAppsCustomizeContent.isDataReady() = "
											+ mAppsCustomizeContent
													.isDataReady());
				}
				sPendingChangedApps.add(new PendingChangedApplications(apps,
						PendingChangedApplications.TYPE_UPDATED));
			} else {
				mAppsCustomizeContent.updateApps(apps);
				// add by zhouerlong
				this.mAppsCustomizeContentSpringWidget.updateApps(apps);
//				this.mAppsCustomizeContentSpringApps.updateApps(apps);
				// add by zhouerlong
			}
		}*/
	}

	/**
	 * A package was uninstalled. We take both the super set of packageNames in
	 * addition to specific applications to remove, the reason being that this
	 * can be called when a package is updated as well. In that scenario, we
	 * only remove specific components from the workspace, where as
	 * package-removal should clear all items by package name.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	// / M: [ALPS01273634] Do not remove shortcut from workspace when receiving
	// ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.
	public void bindComponentsRemoved(final ArrayList<String> packageNames,
			final ArrayList<AppInfo> appInfos, final boolean packageRemoved,
			final boolean permanent) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "bindComponentsRemoved: packageNames = "
					+ packageNames + ", appInfos = " + appInfos
					+ ", packageRemoved = " + packageRemoved + ", permanent = "
					+ permanent);
		}

		Runnable r = new Runnable() {
			public void run() {
				// / M: [ALPS01273634] Do not remove shortcut from workspace
				// when receiving ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.
				bindComponentsRemoved(packageNames, appInfos, packageRemoved,
						permanent);
			}
		};
		/*if (waitUntilResume(r)) {
			return; 
		}*/

		// / M: [ALPS01273634] Do not remove shortcut from workspace when
		// receiving ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.
		if (permanent) {
			if (packageRemoved) {
				mWorkspace.removeItemsByPackageName(packageNames);
			} else {
				mWorkspace.removeItemsByApplicationInfo(appInfos);
			}
		}

		// Notify the drag controller
		mDragController.onAppsRemoved(appInfos, this);

		/*if (!AppsCustomizePagedView.DISABLE_ALL_APPS
				&& mAppsCustomizeContent != null) {
			// / M: delay to handle apps update event if app list is in edit
			// mode, for op09.
			if (mSupportEditAndHideApps
					&& (sUsePendingAppsQueue || !mBindingAppsFinished || !mAppsCustomizeContent
							.isDataReady())) {
				if (LauncherLog.DEBUG) {
					LauncherLog
							.d(TAG,
									"bindAppsRemoved: sUsePendingAppsQueue = "
											+ sUsePendingAppsQueue
											+ ", mBindingAppsFinished = "
											+ mBindingAppsFinished
											+ ", mAppsCustomizeContent.isDataReady() = "
											+ mAppsCustomizeContent
													.isDataReady());
				}
				PendingChangedApplications changedApplications = new PendingChangedApplications(
						null, PendingChangedApplications.TYPE_REMOVED);
				if (changedApplications.removedPackages == null) {
					changedApplications.removedPackages = packageNames;
				} else {
					final int size = packageNames.size();
					String packageName = null;
					for (int i = 0; i <= size; i++) {
						if (!changedApplications.removedPackages
								.contains(packageName)) {
							changedApplications.removedPackages
									.add(packageName);
						}
					}
				}
				sPendingChangedApps.add(changedApplications);
			} else {
				mAppsCustomizeContent.removeApps(appInfos);
			}
		}*/
	}
	
	
	/**
	 * A package was uninstalled. We take both the super set of packageNames in
	 * addition to specific applications to remove, the reason being that this
	 * can be called when a package is updated as well. In that scenario, we
	 * only remove specific components from the workspace, where as
	 * package-removal should clear all items by package name.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	// / M: [ALPS01273634] Do not remove shortcut from workspace when receiving
	// ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.
	public void bindComponentsRemovedFromAppStore(final ArrayList<String> packageNames,
			final ArrayList<AppInfo> appInfos, final boolean packageRemoved,
			final boolean permanent) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "bindComponentsRemoved: packageNames = "
					+ packageNames + ", appInfos = " + appInfos
					+ ", packageRemoved = " + packageRemoved + ", permanent = "
					+ permanent);
		}

		Runnable r = new Runnable() {
			public void run() {
				// / M: [ALPS01273634] Do not remove shortcut from workspace
				// when receiving ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.
				bindComponentsRemoved(packageNames, appInfos, packageRemoved,
						permanent);
			}
		};
		/*if (waitUntilResume(r)) {
			return; 
		}*/

		// / M: [ALPS01273634] Do not remove shortcut from workspace when
		// receiving ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.
		if (permanent) {
			if (packageRemoved) {
				mWorkspace.removeItemsByPackageNameFromAppStore(packageNames.get(0));
			} else {
				mWorkspace.removeItemsByApplicationInfo(appInfos);
			}
		}

		// Notify the drag controller
		mDragController.onAppsRemoved(appInfos, this);
	}

	/**
	 * A number of packages were updated.
	 */
	private ArrayList<Object> mWidgetsAndShortcuts;
	private Runnable mBindPackagesUpdatedRunnable = new Runnable() {
		public void run() {
			bindPackagesUpdated(mWidgetsAndShortcuts);
			mWidgetsAndShortcuts = null;
		}
	};

	public void bindPackagesUpdated(final ArrayList<Object> widgetsAndShortcuts) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "bindPackagesUpdated: sUsePendingAppsQueue = "
					+ sUsePendingAppsQueue);
		}

		if (waitUntilResume(mBindPackagesUpdatedRunnable, true)) {
			mWidgetsAndShortcuts = widgetsAndShortcuts;
			return;
		}

		// Update the widgets pane
		/*if (!AppsCustomizePagedView.DISABLE_ALL_APPS
				&& mAppsCustomizeContent != null && !sUsePendingAppsQueue) { // /
																				// M:
																				// delay
																				// to
																				// update
																				// package
																				// if
																				// app
																				// list
																				// is
																				// in
																				// edit
																				// mode,
																				// for
																				// op09.
//			mAppsCustomizeContent.onPackagesUpdated(widgetsAndShortcuts);
			// add by zhouerlong
			this.mAppsCustomizeContentSpringWidget
					.onPackagesUpdated(widgetsAndShortcuts);
//			this.mAppsCustomizeContentSpringApps
//					.onPackagesUpdated(widgetsAndShortcuts);
			// add by zhouerlong
		}*/
	}

	private int mapConfigurationOriActivityInfoOri(int configOri) {
		final Display d = getWindowManager().getDefaultDisplay();
	
		int naturalOri = Configuration.ORIENTATION_LANDSCAPE;
		switch (d.getRotation()) {
		case Surface.ROTATION_0:
		case Surface.ROTATION_180:
			// We are currently in the same basic orientation as the natural
			// orientation
			naturalOri = configOri;
			break;
		case Surface.ROTATION_90:
		case Surface.ROTATION_270:
			// We are currently in the other basic orientation to the natural
			// orientation
			naturalOri = (configOri == Configuration.ORIENTATION_LANDSCAPE) ? Configuration.ORIENTATION_PORTRAIT
					: Configuration.ORIENTATION_LANDSCAPE;
			break;
		}

		int[] oriMap = { ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
				ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
				ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE };
		// Since the map starts at portrait, we need to offset if this device's
		// natural orientation
		// is landscape.
		int indexOffset = 0;
		if (naturalOri == Configuration.ORIENTATION_LANDSCAPE) {
			indexOffset = 1;
		}
		return oriMap[(d.getRotation() + indexOffset) % 4];
	}

	public boolean isRotationEnabled() {
		boolean enableRotation = sForceEnableRotation
				|| getResources().getBoolean(R.bool.allow_rotation);
		return enableRotation;
	}

	public void lockScreenOrientation() {
		if (isRotationEnabled()) {
			setRequestedOrientation(mapConfigurationOriActivityInfoOri(getResources()
					.getConfiguration().orientation));
		}
	}

	public void unlockScreenOrientation(boolean immediate) {
		if (isRotationEnabled()) {
			if (immediate) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			} else {
				mHandler.postDelayed(new Runnable() {
					public void run() {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
					}
				}, mRestoreScreenOrientationDelay);
			}
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		}
	}

	/* Cling related */
	private boolean isClingsEnabled() {
		if (DISABLE_CLINGS) {
			return false;
		}

		// For now, limit only to phones
		LauncherAppState app = LauncherAppState.getInstance();
		DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
		if (grid.isTablet()) {
			return false;
		}

		// disable clings when running in a test harness
		if (ActivityManager.isRunningInTestHarness())
			return false;

		// Disable clings for accessibility when explore by touch is enabled
		final AccessibilityManager a11yManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
		if (a11yManager.isTouchExplorationEnabled()) {
			return false;
		}

		// Restricted secondary users (child mode) will potentially have very
		// few apps
		// seeded when they start up for the first time. Clings won't work well
		// with that
		// boolean supportsLimitedUsers =
		// android.os.Build.VERSION.SDK_INT >=
		// android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
		// Account[] accounts = AccountManager.get(this).getAccounts();
		// if (supportsLimitedUsers && accounts.length == 0) {
		// UserManager um = (UserManager)
		// getSystemService(Context.USER_SERVICE);
		// Bundle restrictions = um.getUserRestrictions();
		// if (restrictions.getBoolean(UserManager.DISALLOW_MODIFY_ACCOUNTS,
		// false)) {
		// return false;
		// }
		// }
		return true;
	}

	private Cling initCling(int clingId, int scrimId, boolean animate,
			boolean dimNavBarVisibilty) {
		Cling cling = (Cling) findViewById(clingId);
		View scrim = null;
		if (scrimId > 0) {
			scrim = findViewById(R.id.cling_scrim);
		}
		LauncherLog.i(TAG, "initCling: mAttached = " + mAttached
				+ ", mVisible = " + mVisible + ", this = " + this);
		if (cling != null) {
			cling.init(this, scrim);
			cling.show(animate, SHOW_CLING_DURATION);

			if (dimNavBarVisibilty) {
				cling.setSystemUiVisibility(cling.getSystemUiVisibility()
						| View.SYSTEM_UI_FLAG_LOW_PROFILE);
			}
		}
		return cling;
	}

	private void dismissCling(final Cling cling,
			final Runnable postAnimationCb, final String flag, int duration,
			boolean restoreNavBarVisibilty) {
		// To catch cases where siblings of top-level views are made invisible,
		// just check whether
		// the cling is directly set to GONE before dismissing it.
		if (cling != null && cling.getVisibility() != View.GONE) {
			final Runnable cleanUpClingCb = new Runnable() {
				public void run() {
					cling.cleanup();
					// We should update the shared preferences on a background
					// thread
					new Thread("dismissClingThread") {
						public void run() {
							SharedPreferences.Editor editor = mSharedPrefs
									.edit();
							editor.putBoolean(flag, true);
							editor.commit();
						}
					}.start();
					if (postAnimationCb != null) {
						postAnimationCb.run();
					}
				}
			};
			if (duration <= 0) {
				cleanUpClingCb.run();
			} else {
				cling.hide(duration, cleanUpClingCb);
			}
			mHideFromAccessibilityHelper
					.restoreImportantForAccessibility(mDragLayer);

			if (restoreNavBarVisibilty) {
				cling.setSystemUiVisibility(cling.getSystemUiVisibility()
						& ~View.SYSTEM_UI_FLAG_LOW_PROFILE);
			}
		}
	}

	private void removeCling(int id) {
		final View cling = findViewById(id);
		if (cling != null) {
			final ViewGroup parent = (ViewGroup) cling.getParent();
			parent.post(new Runnable() {
				@Override
				public void run() {
					parent.removeView(cling);
				}
			});
			mHideFromAccessibilityHelper
					.restoreImportantForAccessibility(mDragLayer);
		}
	}

	private boolean skipCustomClingIfNoAccounts() {
		Cling cling = (Cling) findViewById(R.id.workspace_cling);
		boolean customCling = cling.getDrawIdentifier().equals(
				"workspace_custom");
		if (customCling) {
			AccountManager am = AccountManager.get(this);
			if (am == null)
				return false;
			Account[] accounts = am.getAccountsByType("com.google");
			return accounts.length == 0;
		}
		return false;
	}

	public void updateCustomContentHintVisibility() {
		Cling cling = (Cling) findViewById(R.id.first_run_cling);
		String ccHintStr = getFirstRunCustomContentHint();

		if (mWorkspace.hasCustomContent()) {
			// Show the custom content hint if ccHintStr is not empty
			if (cling != null) {
				setCustomContentHintVisibility(cling, ccHintStr, true, true);
			}
		} else {
			// Hide the custom content hint
			if (cling != null) {
				setCustomContentHintVisibility(cling, ccHintStr, false, true);
			}
		}
	}

	private void setCustomContentHintVisibility(Cling cling, String ccHintStr,
			boolean visible, boolean animate) {
		final TextView ccHint = (TextView) cling
				.findViewById(R.id.custom_content_hint);
		if (ccHint != null) {
			if (visible && !ccHintStr.isEmpty()) {
				ccHint.setText(ccHintStr);
				ccHint.setVisibility(View.VISIBLE);
				if (animate) {
					ccHint.setAlpha(0f);
					ccHint.animate().alpha(1f).setDuration(SHOW_CLING_DURATION)
							.start();
				} else {
					ccHint.setAlpha(1f);
				}
			} else {
				if (animate) {
					ccHint.animate().alpha(0f).setDuration(SHOW_CLING_DURATION)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									ccHint.setVisibility(View.GONE);
								}
							}).start();
				} else {
					ccHint.setAlpha(0f);
					ccHint.setVisibility(View.GONE);
				}
			}
		}
	}

	public void showFirstRunCling() {
		if (isClingsEnabled()
				&& !mSharedPrefs.getBoolean(
						Cling.FIRST_RUN_CLING_DISMISSED_KEY, false)
				&& !skipCustomClingIfNoAccounts()) {
			// If we're not using the default workspace layout, replace
			// workspace cling
			// with a custom workspace cling (usually specified in an overlay)
			// For now, only do this on tablets
			if (!DISABLE_CUSTOM_CLINGS) {
				if (mSharedPrefs.getInt(
						LauncherProvider.DEFAULT_WORKSPACE_RESOURCE_ID, 0) != 0
						&& getResources().getBoolean(
								R.bool.config_useCustomClings)) {
					// Use a custom cling
					View cling = findViewById(R.id.workspace_cling);
					ViewGroup clingParent = (ViewGroup) cling.getParent();
					int clingIndex = clingParent.indexOfChild(cling);
					clingParent.removeViewAt(clingIndex);
					View customCling = mInflater
							.inflate(R.layout.custom_workspace_cling,
									clingParent, false);
					clingParent.addView(customCling, clingIndex);
					customCling.setId(R.id.workspace_cling);
				}
			}
			Cling cling = (Cling) findViewById(R.id.first_run_cling);
			if (cling != null) {
				String sbHintStr = getFirstRunClingSearchBarHint();
				String ccHintStr = getFirstRunCustomContentHint();
				if (!sbHintStr.isEmpty()) {
					TextView sbHint = (TextView) cling
							.findViewById(R.id.search_bar_hint);
					sbHint.setText(sbHintStr);
					sbHint.setVisibility(View.VISIBLE);
				}
				setCustomContentHintVisibility(cling, ccHintStr, true, false);
			}
			initCling(R.id.first_run_cling, 0, false, true);
		} else {
			removeCling(R.id.first_run_cling);
		}
	}

	protected String getFirstRunClingSearchBarHint() {
		return "";
	}

	protected String getFirstRunCustomContentHint() {
		return "";
	}

	protected int getFirstRunFocusedHotseatAppDrawableId() {
		return -1;
	}

	protected ComponentName getFirstRunFocusedHotseatAppComponentName() {
		return null;
	}

	protected int getFirstRunFocusedHotseatAppRank() {
		return -1;
	}

	protected String getFirstRunFocusedHotseatAppBubbleTitle() {
		return "";
	}

	protected String getFirstRunFocusedHotseatAppBubbleDescription() {
		return "";
	}

	public void showFirstRunWorkspaceCling() {
		// Enable the clings only if they have not been dismissed before
		if (isClingsEnabled()
				&& !mSharedPrefs.getBoolean(
						Cling.WORKSPACE_CLING_DISMISSED_KEY, false)) {
			Cling c = initCling(R.id.workspace_cling, 0, false, true);

			// Set the focused hotseat app if there is one
			c.setFocusedHotseatApp(getFirstRunFocusedHotseatAppDrawableId(),
					getFirstRunFocusedHotseatAppRank(),
					getFirstRunFocusedHotseatAppComponentName(),
					getFirstRunFocusedHotseatAppBubbleTitle(),
					getFirstRunFocusedHotseatAppBubbleDescription());
		} else {
			removeCling(R.id.workspace_cling);
		}
	}

	public Cling showFirstRunFoldersCling() {
		// Enable the clings only if they have not been dismissed before
		if (isClingsEnabled()
				&& !mSharedPrefs.getBoolean(Cling.FOLDER_CLING_DISMISSED_KEY,
						false)) {
			Cling cling = initCling(R.id.folder_cling, R.id.cling_scrim, true,
					true);
			return cling;
		} else {
			removeCling(R.id.folder_cling);
			return null;
		}
	}

	protected SharedPreferences getSharedPrefs() {
		return mSharedPrefs;
	}

	public boolean isFolderClingVisible() {
		Cling cling = (Cling) findViewById(R.id.folder_cling);
		if (cling != null) {
			return cling.getVisibility() == View.VISIBLE;
		}
		return false;
	}

	public void dismissFirstRunCling(View v) {
		Cling cling = (Cling) findViewById(R.id.first_run_cling);
		Runnable cb = new Runnable() {
			public void run() {
				// Show the workspace cling next
				showFirstRunWorkspaceCling();
			}
		};
		dismissCling(cling, cb, Cling.FIRST_RUN_CLING_DISMISSED_KEY,
				DISMISS_CLING_DURATION, false);

		// Fade out the search bar for the workspace cling coming up
		mSearchDropTargetBar.hideSearchBar(true);
	}

	public void dismissWorkspaceCling(View v) {
		Cling cling = (Cling) findViewById(R.id.workspace_cling);
		Runnable cb = null;
		if (v == null) {
			cb = new Runnable() {
				public void run() {
					mWorkspace.enterOverviewMode();
				}
			};
		}
		dismissCling(cling, cb, Cling.WORKSPACE_CLING_DISMISSED_KEY,
				DISMISS_CLING_DURATION, true);

		// Fade in the search bar
		mSearchDropTargetBar.showSearchBar(true);
	}

	public void dismissFolderCling(View v) {
		Cling cling = (Cling) findViewById(R.id.folder_cling);
		dismissCling(cling, null, Cling.FOLDER_CLING_DISMISSED_KEY,
				DISMISS_CLING_DURATION, true);
	}

	/**
	 * Prints out out state for debugging.
	 */
	public void dumpState() {
		Log.d(TAG, "BEGIN launcher3 dump state for launcher " + this);
		Log.d(TAG, "mSavedState=" + mSavedState);
		Log.d(TAG, "mWorkspaceLoading=" + mWorkspaceLoading);
		Log.d(TAG, "mRestoring=" + mRestoring);
		Log.d(TAG, "mWaitingForResult=" + mWaitingForResult);
		Log.d(TAG, "mSavedInstanceState=" + mSavedInstanceState);
		Log.d(TAG, "sFolders.size=" + sFolders.size());
		mModel.dumpState();

		/*if (mAppsCustomizeContent != null) {
			mAppsCustomizeContent.dumpState();
		}*/
		Log.d(TAG, "END launcher3 dump state");
	}

	@Override
	public void dump(String prefix, FileDescriptor fd, PrintWriter writer,
			String[] args) {
		super.dump(prefix, fd, writer, args);
		synchronized (sDumpLogs) {
			writer.println(" ");
			writer.println("Debug logs: ");
			for (int i = 0; i < sDumpLogs.size(); i++) {
				writer.println("  " + sDumpLogs.get(i));
			}
		}
	}

	public static void dumpDebugLogsToConsole() {
		if (DEBUG_DUMP_LOG) {
			synchronized (sDumpLogs) {
				Log.d(TAG, "");
				Log.d(TAG, "*********************");
				Log.d(TAG, "Launcher debug logs: ");
				for (int i = 0; i < sDumpLogs.size(); i++) {
					Log.d(TAG, "  " + sDumpLogs.get(i));
				}
				Log.d(TAG, "*********************");
				Log.d(TAG, "");
			}
		}
	}

	public static void addDumpLog(String tag, String log, boolean debugLog) {
		if (debugLog) {
			Log.d(tag, log);
		}
		if (DEBUG_DUMP_LOG) {
			sDateStamp.setTime(System.currentTimeMillis());
			synchronized (sDumpLogs) {
				sDumpLogs.add(sDateFormat.format(sDateStamp) + ": " + tag
						+ ", " + log);
			}
		}
	}

	public void dumpLogsToLocalData() {
		if (DEBUG_DUMP_LOG) {
			new Thread("DumpLogsToLocalData") {
				@Override
				public void run() {
					boolean success = false;
					sDateStamp.setTime(sRunStart);
					String FILENAME = sDateStamp.getMonth() + "-"
							+ sDateStamp.getDay() + "_" + sDateStamp.getHours()
							+ "-" + sDateStamp.getMinutes() + "_"
							+ sDateStamp.getSeconds() + ".txt";

					FileOutputStream fos = null;
					File outFile = null;
					try {
						outFile = new File(getFilesDir(), FILENAME);
						outFile.createNewFile();
						fos = new FileOutputStream(outFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (fos != null) {
						PrintWriter writer = new PrintWriter(fos);

						writer.println(" ");
						writer.println("Debug logs: ");
						synchronized (sDumpLogs) {
							for (int i = 0; i < sDumpLogs.size(); i++) {
								writer.println("  " + sDumpLogs.get(i));
							}
						}
						writer.close();
					}
					try {
						if (fos != null) {
							fos.close();
							success = true;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	/**
	 * M: Get current CellLayout bounds.
	 * 
	 * @return mCurrentBounds.
	 */
	Rect getCurrentBounds() {
		return mCurrentBounds;
	}

	/**
	 * M: Show long press widget to add message, avoid duplication of message.
	 */
	public void showLongPressWidgetToAddMessage() {
		if (mLongPressWidgetToAddToast == null) {
			mLongPressWidgetToAddToast = Toast.makeText(
					getApplicationContext(), R.string.long_press_widget_to_add,
					Toast.LENGTH_SHORT);
		} else {
			mLongPressWidgetToAddToast
					.setText(R.string.long_press_widget_to_add);
			mLongPressWidgetToAddToast.setDuration(Toast.LENGTH_SHORT);
		}
		mLongPressWidgetToAddToast.show();
	}

	/**
	 * M: Cancel long press widget to add message when press back key.
	 */
	private void cancelLongPressWidgetToAddMessage() {
		if (mLongPressWidgetToAddToast != null) {
			mLongPressWidgetToAddToast.cancel();
		}
	}

	/**
	 * M: Set orientation changed flag, this would make the apps customized pane
	 * recreate views in certain condition.
	 */
	public void notifyOrientationChanged() {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG,
					"notifyOrientationChanged: mOrientationChanged = "
							+ mOrientationChanged + ", mPaused = " + mPaused);
		}
		mOrientationChanged = true;
	}

	/**
	 * M: Tell Launcher that the pages in app customized pane were recreated.
	 */
	void notifyPagesWereRecreated() {
		mPagesWereRecreated = true;
	}

	/**
	 * M: Reset re-sync apps pages flags.
	 */
	private void resetReSyncFlags() {
		mOrientationChanged = false;
		mPagesWereRecreated = false;
	}

	/**
	 * M: Volunteer free memory when system low memory.
	 */
	private void volunteerFreeMemory() {
//		mAppsCustomizeTabHost.onTrimMemory();

		// / M: Free more memory than AOSP
		mIconCache.flush();
	}

	/**
	 * M: Make the apps customize pane enter edit mode, user can rearrange the
	 * application icons in this mode, add for OP09 start.
	 */
	public void enterEditMode() {
		if (LauncherLog.DEBUG_EDIT) {
			LauncherLog.d(TAG, "enterEditMode: mIsInEditMode = "
					+ sIsInEditMode);
		}

		sIsInEditMode = true;
		final View marketButton = findViewById(R.id.market_button);
		marketButton.setVisibility(View.GONE);

//		mAppsCustomizeTabHost.enterEditMode();
	}

	/**
	 * M: Make the apps customize pane exit edit mode.
	 */
	public void exitEditMode() {
		if (LauncherLog.DEBUG_EDIT) {
			LauncherLog
					.d(TAG, "exitEditMode: mIsInEditMode = " + sIsInEditMode);
		}

		sIsInEditMode = false;
		updateAppMarketIcon();
//		mAppsCustomizeTabHost.exitEditMode();
	}

	/**
	 * M: Whether the apps customize pane is in edit mode.
	 * 
	 * @return
	 */
	public static boolean isInEditMode() {
		return sIsInEditMode;
	}

	
	public  boolean isInHideViewModel() {
			return mDragLayer.indexOfChild(mHideView)!=-1;
	}
	/**
	 * M: Update view visibility and icon resource of the given view.
	 * 
	 * @param viewId
	 * @param drawableResId
	 * @param visible
	 */
	private void updateVisibilityAndIconResource(final int viewId,
			final int drawableResId, final boolean visible) {
		final TextView textButton = (TextView) findViewById(viewId);
		if (visible) {
			final Resources r = getResources();
			final int w = r
					.getDimensionPixelSize(R.dimen.toolbar_external_icon_width);
			final int h = r
					.getDimensionPixelSize(R.dimen.toolbar_external_icon_height);

			final Drawable iconDrawable = r.getDrawable(drawableResId);
			iconDrawable.setBounds(0, 0, w, h);
			textButton.setVisibility(View.VISIBLE);
			textButton.setCompoundDrawables(iconDrawable, null, null, null);
		} else {
			textButton.setVisibility(View.GONE);
		}
	}

	/**
	 * M: Hanlde up button click event.
	 * 
	 * @param v
	 */
	public void onClickHomeAndUpButton(View v) {
		exitEditMode();
	}

	/**
	 * M: Click delete button will uinstall the app in edit mode.
	 * 
	 * @param v
	 */
	public void onClickDeleteButton(View v) {
		final AppInfo info = (AppInfo) ((PagedViewIcon) v).getTag();
		startApplicationUninstallActivity(info);
	}

	// add by zhouerlong
	public void OnUninstallClick(View v) {
		final ShortcutInfo info = (ShortcutInfo) ((BubbleTextView) v).getTag();
		startApplicationUninstallActivity(info);
	}

	// add by zhouerlong

	/**
	 * M: Start hideAppsActivity.
	 */
	private void startHideAppsActivity() {
		Intent intent = new Intent();
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setClassName(HIDE_PACKAGE_NAME, HIDE_ACTIVITY_NAME);
		startActivityForResultSafely(intent, REQUEST_HIDE_APPS);
	}

	/**
	 * M: Enable pending apps queue to block all package add/change/removed
	 * events to protect the right order in apps customize paged view, this
	 * would be called when entering edit mode.
	 */
	static void enablePendingAppsQueue() {
		sUsePendingAppsQueue = true;
	}

	/**
	 * M: Disable pending queue and flush pending queue to handle all pending
	 * package add/change/removed events.
	 * 
	 * @param appsCustomizePagedView
	 */
	static void disableAndFlushPendingAppsQueue(
			AppsCustomizePagedView appsCustomizePagedView) {
		sUsePendingAppsQueue = false;
		flushPendingAppsQueue(appsCustomizePagedView);
	}

	/**
	 * M: Flush pending queue and handle all pending package add/change/removed
	 * events.
	 * 
	 * @param appsCustomizePagedView
	 */
	static void flushPendingAppsQueue(
			AppsCustomizePagedView appsCustomizePagedView) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "flushPendingAppsQueue: numbers = "
					+ sPendingChangedApps.size());
		}
		Iterator<PendingChangedApplications> iter = sPendingChangedApps
				.iterator();
		// TODO: maybe we can optimize this to avoid some applications
		// installed/uninstall/changed many times during user in edit mode.
		final boolean listEmpty = sPendingChangedApps.isEmpty();
		while (iter.hasNext()) {
			processPendingChangedApplications(appsCustomizePagedView,
					iter.next());
			iter.remove();
		}

		if (!listEmpty) {
			appsCustomizePagedView.processPendingPost();
			appsCustomizePagedView.onPackagesUpdated(LauncherModel
					.getSortedWidgetsAndShortcuts(LauncherAppState
							.getInstance().getContext()));
		}
	}
	
	public void setCustomTheme(String destPath) {

//		ThemeInfoUtils.doChangeTheme(this, destPath, this);
		 
	}
	
	

	

	/**
	 * M: Process pending changes application list, these apps are changed
	 * during editing all apps list.
	 */
	private static void processPendingChangedApplications(
			AppsCustomizePagedView appsCustomizePagedView,
			PendingChangedApplications pendingApps) {
		if (LauncherLog.DEBUG_EDIT) {
			LauncherLog.d(TAG, "processPendingChangedApplications: type = "
					+ pendingApps.type + ",apps = " + pendingApps.appInfos);
		}

		switch (pendingApps.type) {
		case PendingChangedApplications.TYPE_ADDED:
			appsCustomizePagedView.processPendingAddApps(pendingApps.appInfos);
			break;
		case PendingChangedApplications.TYPE_UPDATED:
			appsCustomizePagedView
					.processPendingUpdateApps(pendingApps.appInfos);
			break;
		case PendingChangedApplications.TYPE_REMOVED:
			appsCustomizePagedView
					.processPendingRemoveApps(pendingApps.removedPackages);
			break;
		default:
			break;
		}
	}

	/**
	 * M: Class used to record pending add/change/removed applications.
	 */
	private static class PendingChangedApplications {
		public static final int TYPE_ADDED = 0;
		public static final int TYPE_UPDATED = 1;
		public static final int TYPE_REMOVED = 2;

		ArrayList<String> removedPackages;
		ArrayList<AppInfo> appInfos;
		int type;

		public PendingChangedApplications(ArrayList<AppInfo> apps, int t) {
			appInfos = apps;
			type = t;
		}
	}

	/** add for OP09 end */

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			Trace.traceBegin(Trace.TRACE_TAG_INPUT,
					"Launcher.dispatchTouchEvent:ACTION_DOWN");
		} else if (ev.getAction() == MotionEvent.ACTION_UP) {
			Trace.traceBegin(Trace.TRACE_TAG_INPUT,
					"Launcher.dispatchTouchEvent:ACTION_UP");
		}
		Trace.traceEnd(Trace.TRACE_TAG_INPUT);
		return super.dispatchTouchEvent(ev);
	}

	public void OnSnapToRightPage() {
		this.mWorkspace.snapToRightPage(true);
	}

	@Override
	public void bindAppsFinish() {

		if (getHotseat() != null)  {
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					getHotseat().reLayoutContent();
					
					if (LqShredPreferences.isLqtheme(Launcher.this)) {
			        FindDefaultResoures f = new FindDefaultResoures(Launcher.this);
			        Runnable  wall = new Runnable() {
						
						@Override
						public void run() {
							mThemeListView.init(Launcher.this,
									R.layout.themes_list_item);
						}
					};
					Runnable  theme = new Runnable() {
						
						@Override
						public void run() {
							mWalListView.init(Launcher.this, R.layout.wallpapers_list_item);
						}
					};
			        f.loadWallPapaperAndTHeme(wall, theme);
					}
					if(isSupportHideApp) {
					bindHideApps();
					}
				}
			},800);
		}
		
		
	}

public void updateChildContent(int id) {
	if (id != CellLayout.EXTRA_EMPTY_SCREEN_ID)
	mNavigationLayout.updateChildContent(id, mWorkspace);
}
	//add by xxf
	public View getPageIndicators() {
		return mPageIndicators;
	}
	@Override
	public void suggestWallpaperDimension(boolean isoffset) {
		int w = isoffset?viewWidth*2:viewWidth;
		int h = viewHeight;
//		this.updateWallpaperDimensions(w,h);
	}
	
	
	public void OnDoubleTap() {
		try {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
			pm.goToSleep(SystemClock.uptimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
		wl.acquire();
		wl.release();
		
	}
	@Override
	public void bindAppUpdated(final AppInfo app) {
		if (LauncherLog.DEBUG) {
			LauncherLog.d(TAG, "bindAppsUpdated: apps = " + app);
		}

		Runnable r = new Runnable() {
			public void run() {
				bindAppUpdated(app);
			}
		};
		if (waitUntilResume(r)) {
			return;
		}

		if (mWorkspace != null) {
			mWorkspace.updateShortcutsFromAppstore(app);
		}
	
		
	}
	@Override
	public void bindDownProgress(DownLoadTaskInfo info) {
		mWorkspace.updateDownProgressFromAppStore(info);
		
	}
	


    

	@Override
	public void screenShotWorkspace() {
		// TODO Auto-generated method stub
		
	}
	
	boolean successful=false;
	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();  
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴    
        float[] values = event.values;  
        if (sensorType == Sensor.TYPE_ACCELEROMETER){  
            //这里可以调节摇一摇的灵敏度
            if ((Math.abs(values[0]) > SENSOR_VALUE || Math.abs(values[1]) > SENSOR_VALUE || Math.abs(values[2]) > SENSOR_VALUE)&& getworkspace().isInSpringLoadMoed()){
            	LogUtils.i("zhouerlong","sensor value == " + " " + values[ 0 ] + " " + values[ 1 ] + " " +  values[ 2 ] );
            	final Runnable r =new Runnable() {
					
					@Override
					public void run() {
						successful = false;
						
					}
				};
            	if(!successful) {
            		successful = true;
            		mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
		                	alignmentUpForCurrentCellLayout(r);
						}
					}, 800);
            	}
            }  
        }  
	
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTransitionStart(Animator animator, Animation animation) {
		mLauncherBackground.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		mWallpaperBg.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		mExplosionDialogView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		mExplosionDialogView.setVisibility(View.VISIBLE);
		onTranslationContainDeleteTarget();
		
	}
	@Override
	public void onTransitionEnd(Animator animator, Animation animation) {
		mLauncherBackground.setLayerType(View.LAYER_TYPE_NONE, null);
		mWallpaperBg.setLayerType(View.LAYER_TYPE_NONE, null);
		mExplosionDialogView.setLayerType(View.LAYER_TYPE_NONE, null);
	}
}

interface LauncherTransitionable {
	View getContent();

	void onLauncherTransitionPrepare(Launcher l, boolean animated,
			boolean toWorkspace);

	void onLauncherTransitionStart(Launcher l, boolean animated,
			boolean toWorkspace);

	void onLauncherTransitionStep(Launcher l, float t);

	void onLauncherTransitionEnd(Launcher l, boolean animated,
			boolean toWorkspace);
}