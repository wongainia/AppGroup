package com.android.launcher3.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher3.BlueTask;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DeleteDropTarget.ExplosionFinishTask;
import com.android.launcher3.DecelerateInterpolator;
import com.android.launcher3.DragLayer;
import com.android.launcher3.Hotseat;
import com.android.launcher3.IconCache;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherUnlock;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.LauncherFolderAnimations;
import com.android.launcher3.anim.TransitionAnims.TransitionListener;
import com.android.launcher3.explosion.ExplosionField;

public class ExplosionView extends LinearLayout implements OnClickListener {

	private Button mOk;
	public  Button mcancel;
	private BubbleTextView mIcon;
	private boolean mUninstallSuccessful=false;
	int[] from = new int[2];
	int[] to = new int[2];
	private float mProgressByWorkspace=0;
	public void setProgressByWorkspace(float mProgressByWorkspace) {
		this.mProgressByWorkspace = mProgressByWorkspace;
	}

	private static final int DURATION_OPEN_CLOSE = 1200;
	private Interpolator mZoomInInterpolator;
	private Launcher mLauncher;

	public ExplosionView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public ExplosionView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ExplosionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLauncher = (Launcher) context;
		mZoomInInterpolator = AnimationUtils.loadInterpolator(context,
				com.android.internal.R.interpolator.decelerate_cubic);
	}

	public ExplosionView(Context context) {
		super(context);
	}

	private void centerAboutIcon(int[] from, int[] to) {
		DragLayer.LayoutParams lp = (DragLayer.LayoutParams) getLayoutParams();
		int width = this.getResources().getDimensionPixelOffset(
				R.dimen.uninstallExplosion_width);
		int height = this.getResources().getDimensionPixelOffset(
				R.dimen.uninstallExplosion_height);
		lp.width = width;
		lp.height = height;

		Display display = ((Activity) this.getContext()).getWindowManager()
				.getDefaultDisplay();
		float wCenterX = (display.getWidth() - width) / 2;

		from[0] = (int) wCenterX;
		from[1] = -height;
		to[0] = (int) wCenterX;
		to[1] = height/2;
	}

	private Runnable mRunnbale;
	private ExplosionField mExplosionField;

	public ExplosionField getExplosionField() {
		return mExplosionField;
	}

	public void setExplosionField(ExplosionField mExplosionField) {
		this.mExplosionField = mExplosionField;
	}

	/**
	 * 文件夹打开动画
	 */
	public void animateOpen(final Runnable r) {
		mRunnbale = r;
		if (!(getParent() instanceof DragLayer))
			return;
		WallpaperManager wm = WallpaperManager.getInstance(mLauncher);
		this.bringToFront();

		centerAboutIcon(from, to);
		this.setAlpha(1f);
		LauncherFolderAnimations mFolderAnimator = new LauncherFolderAnimations(
				mZoomInInterpolator);
		
		mFolderAnimator.play(this, new float[] { 1f, 1f },
				new float[] { 1f, 1f }, from, to);
		ValueAnimator update =  ValueAnimator.ofFloat(0f,1f);
		ObjectAnimator iconT = ObjectAnimator.ofFloat(mIcon, "translationY", from[1],0);
		iconT.setDuration(1800);
		iconT.setInterpolator(new DecelerateInterpolator());
		iconT.start();
		
		ObjectAnimator text = ObjectAnimator.ofFloat(mText, "translationY", from[1],0);
		text.setDuration(1500);
		text.setInterpolator(new DecelerateInterpolator());
		text.start();
		ObjectAnimator linear = ObjectAnimator.ofFloat(mLinear, "translationY", from[1],0);
		linear.setDuration(1300);
		linear.setInterpolator(new DecelerateInterpolator());
		linear.start();
		
		ValueAnimator updateByBlue =  ValueAnimator.ofFloat(0f,1-mProgressByWorkspace);

		updateByBlue.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float percent = (float) animation.getAnimatedValue();
				mLauncher.updateAlphaByExplosionView(percent,true);
			}
		});
		update.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float percent = (float) animation.getAnimatedValue();
				setAlpha(percent);
				mLauncher.updateTranslationYbyExplosionView(percent,true,false);
			}
		});
		mFolderAnimator.setListener(mLauncher/*new TransitionListener() {

			public void onTransitionStart(Animator animator, Animation animation) {

				setLayerType(View.LAYER_TYPE_NONE, null);
			}

			public void onTransitionEnd(Animator animator, Animation animation) {
			}
		}*/);
		mFolderAnimator.setDuration(DURATION_OPEN_CLOSE);
		mFolderAnimator.getAnimatorSet().play(update);
		mFolderAnimator.getAnimatorSet().play(updateByBlue);
		mFolderAnimator.setInterpolator(mZoomInInterpolator);
		mFolderAnimator.start();
	}

	
	public boolean isOpen() {
			DragLayer parent = (DragLayer) getParent();
			
			if (parent !=null&&parent.indexOfChild(this) !=-1) {
				return true;
			}
			return false;
	}

	private boolean isOpen=false;
	private boolean isClose=false;
	private View mLinear;
	private TextView mText;
	
	public boolean isClose() {
		return isClose;
	}

	public void setClose(boolean isClose) {
		this.isClose = isClose;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	/**
	 * 文件夹关闭动画
	 */
	public void animateClosed() {
		if (!(getParent() instanceof DragLayer))
			return;
		if(isClose) {
			return;
		}
		isClose=true;
		final LauncherFolderAnimations mFolderAnimator = new LauncherFolderAnimations(
				mZoomInInterpolator);

		mFolderAnimator.play(this, new float[] { 1f, 1f },
				new float[] { 1f, 1f }, to, from);
		
		

		mFolderAnimator.setListener(new TransitionListener() {

			@Override
			public void onTransitionStart(Animator animator, Animation animation) {
				setLayerType(LAYER_TYPE_HARDWARE, null);
				mLauncher.getWallpaperBg().setLayerType(LAYER_TYPE_HARDWARE, null);

        		if(!mLauncher.getworkspace().isInSpringLoadMoed()) {

//        			if(Utilities.supportUnlockAnim()) {
//				LauncherUnlock.getInstace().onPause(mLauncher.getWorkspace().getCurrentLayout(), mLauncher.getHotseat().getLayout(),mLauncher,3*1000,false);
//				LauncherUnlock.getInstace().unlock();
//        			}
        		}
//				mLauncher.getworkspace().setLayerType(LAYER_TYPE_HARDWARE, null);
//				mLauncher.getLauncherBackground().setLayerType(LAYER_TYPE_HARDWARE, null);
			}

			@Override
			public void onTransitionEnd(Animator animator, Animation animation) {
				setLayerType(LAYER_TYPE_NONE, null);
				isOpen=false;
					isClose=false;
				mLauncher.getWallpaperBg().revert();
				mLauncher.getWallpaperBg().setLayerType(LAYER_TYPE_NONE, null);
//				mLauncher.getLauncherBackground().setLayerType(LAYER_TYPE_NONE, null);
				mLauncher.getworkspace().setLayerType(LAYER_TYPE_NONE, null);
				mLauncher.getWallpaperBg().setVisibility(View.GONE);
				onCloseComplete();
				mLauncher.onCloseComplete();
				if (mRunnbale != null&& !mUninstallSuccessful) {
					mRunnbale.run();
				}
				mUninstallSuccessful=false;
				setVisibility(View.GONE);
		        mLauncher.enterDeleteDropTarget(false);
	            if(!mLauncher.getworkspace().isInSpringLoadMoed()&&mLauncher.getPageIndicators().getSystemUiVisibility()!=View.SYSTEM_UI_FLAG_VISIBLE) {
	            	mLauncher.getPageIndicators().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
	        	}

			}
		});

		ValueAnimator update =  ValueAnimator.ofFloat(1f,0f);
		ValueAnimator updateByProgresss =  ValueAnimator.ofFloat(1f,0f);
		updateByProgresss.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {

				float percent = (float) animation.getAnimatedValue();
				setAlpha(percent);
				mLauncher.updateAlphaByExplosionView(percent,false);
			}
		});
		update.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {

				float percent = (float) animation.getAnimatedValue();
				mLauncher.updateTranslationYbyExplosionView(percent,false,false);
			}
		});
		mFolderAnimator.setDuration(DURATION_OPEN_CLOSE);
		mFolderAnimator.getAnimatorSet().play(updateByProgresss);
		mFolderAnimator.getAnimatorSet().play(update);
		mFolderAnimator.setInterpolator(mZoomInInterpolator);
		mFolderAnimator.start();
	}

	private void onCloseComplete() {
		if (this.getParent() != null) {
			DragLayer parent = (DragLayer) getParent();
			parent.removeView(this);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mOk = (Button) findViewById(R.id.uninstall_ex);
		mOk.setOnClickListener(this);
		mIcon = (BubbleTextView) findViewById(R.id.icon);
		mText = (TextView) findViewById(R.id.description);
		mLinear = findViewById(R.id.btn_linear);
		mcancel = (Button) findViewById(R.id.cancel_uninstall_ex);
		mcancel.setOnClickListener(this);

	}

	public void applyFromShortcutInfo(IconCache cache, ShortcutInfo info) {

		mIcon.setScaleX(1);
		mIcon.setScaleY(1);
		mIcon.setAlpha(1);
		mIcon.setCompoundDrawablePadding((int) getResources().getDimension(
				R.dimen.icon_drawable_padding));
		mIcon.applyFromShortcutInfo(info, cache);
	}

	public BubbleTextView getIcon() {
		return mIcon;
	}

	public void setIcon(BubbleTextView mIcon) {
		this.mIcon = mIcon;
	}
	
	public void onBackPress() {
		if(!mUninstallSuccessful) {
			if (mRunnbale !=null ) {
				if (mRunnbale instanceof DeleteDropTarget.ExplosionFinishTask) {
					ExplosionFinishTask run = (ExplosionFinishTask) mRunnbale;
					run.setUninstallSuccessful(false);
					mUninstallSuccessful = false;
				}
			}
			animateClosed();
		}
	}

	@Override
	public void onClick(View v) {

		if(isOpen) {
			return;
		}
		isOpen=true;
		
		if (v.getId() == R.id.uninstall_ex) {
			if(mExplosionField !=null) {
				mExplosionField.bringToFront();
				mExplosionField.explode(getIcon(),mRunnbale);
//				mcancel.setClickable(false);
				if (mRunnbale !=null ) {
					if (mRunnbale instanceof DeleteDropTarget.ExplosionFinishTask) {
						ExplosionFinishTask run = (ExplosionFinishTask) mRunnbale;
						run.setUninstallSuccessful(true);
						mUninstallSuccessful = true;
					}
				}
			}
		}
		if (v.getId() == R.id.cancel_uninstall_ex) {
			if (mRunnbale !=null ) {
				if (mRunnbale instanceof DeleteDropTarget.ExplosionFinishTask) {
					ExplosionFinishTask run = (ExplosionFinishTask) mRunnbale;
					run.setUninstallSuccessful(false);
					
					mUninstallSuccessful = false;
				}
			}
			animateClosed();
		}

	}

}