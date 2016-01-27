package saulmm.myapplication;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

@SuppressWarnings("unused")
public class AvatarImageBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private final static String TAG = "behavior";
    private final Context _context;

    private float _dependencyMaxScroll;
    private float _minScrollDistance;


    private int _statusBarHeight;

    private int _startMarginLeft;
    private int _startMarginTop;
    private int _startHeight;

    private int _finalMarginLeft;
    private int _finalMarginTop;
    private int _finalHeight;

    private int _toolbarHeight;

    private CoordinatorLayout.LayoutParams _startParams;


    public AvatarImageBehavior(Context context, AttributeSet attrs) {
        _context = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        if (dependency instanceof Toolbar) {
            _toolbarHeight = dependency.getLayoutParams().height;
        }
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        maybeInitProperties(child, dependency);

        final float currentScrollDistance = (float) (dependency.getBottom() - _minScrollDistance);

        float expandedPercentageFactor = currentScrollDistance / _dependencyMaxScroll;

        float heightToSubtract = ((_startHeight - _finalHeight) * (1f - expandedPercentageFactor));

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();

        lp.width = (int) (_startHeight - heightToSubtract);
        lp.height = (int) (_startHeight - heightToSubtract);

        float distanceYToSubtract = ((_startMarginTop - _finalMarginTop) * (1f - expandedPercentageFactor * expandedPercentageFactor)) + (lp.height / 2);
        float distanceXToSubtract = ((_startMarginLeft - _finalMarginLeft) * (1f - expandedPercentageFactor * (2 - expandedPercentageFactor))) + (lp.width / 2);

        lp.topMargin = _startMarginTop - (int) distanceYToSubtract;
        lp.leftMargin = _startMarginLeft - (int) distanceXToSubtract;

        child.setLayoutParams(lp);
        return true;
    }

    private void maybeInitProperties(View child, View dependency) {
        if (_startMarginTop == 0) {
            _startMarginTop = (int) child.getY();
        }

        if (_startMarginLeft == 0) {
            _startMarginLeft = (int) (child.getX() + child.getWidth() / 2);
        }

        if (_finalMarginTop == 0) {
            _finalMarginTop = dependency.getHeight() / 2;
        }
        if (_finalMarginLeft == 0) {
            _finalMarginLeft = _context.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material);
        }


        if (_finalHeight == 0) {
            _finalHeight = _context.getResources().getDimensionPixelOffset(R.dimen.image_final_width);
        }

        if (_startHeight == 0) {
            _startHeight = child.getHeight();
        }

        if (_startParams == null) {
            //reset layoutParams and set current margin
            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(child.getWidth(), child.getHeight());
            params.topMargin = _startMarginTop;
            params.leftMargin = _startMarginLeft;
            params.setBehavior(this);
            child.setLayoutParams(params);
            _startParams = params;
        }

        if (_dependencyMaxScroll == 0) {
            _minScrollDistance = (_toolbarHeight / 2) + getStatusBarHeight();
            _dependencyMaxScroll = dependency.getBottom() - _minScrollDistance;
        }
    }

    public int getStatusBarHeight() {
        if (_statusBarHeight == 0) {
            int result = 0;
            int resourceId = _context.getResources().getIdentifier("status_bar_height", "dimen", "android");

            if (resourceId > 0) {
                result = _context.getResources().getDimensionPixelSize(resourceId);
            }
            _statusBarHeight = result;
        }

        return _statusBarHeight;
    }

    private int convertDpToPx(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, _context.getResources().getDisplayMetrics());
    }
}
