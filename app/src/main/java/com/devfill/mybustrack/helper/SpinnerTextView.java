package com.devfill.mybustrack.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.devfill.mybustrack.R;
import com.devfill.mybustrack.ui.MainActivity;

import static java.security.AccessController.getContext;

public class SpinnerTextView extends AppCompatTextView implements View.OnClickListener {

    private String mPrompt;
    private int mSelection;
    private ISpinnerTextViewListener mListener;

    public interface ISpinnerTextViewListener {
        void onItemSelected(int position);
    }

    public SpinnerTextView(Context context) {
        super(context);
        init(null);
    }
    public SpinnerTextView() {
        super(null);

    }
    public SpinnerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SpinnerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SpinnerTextView);

            mPrompt = typedArray.getString(R.styleable.SpinnerTextView_android_prompt);
            typedArray.recycle();
        }

        mSelection = -1;
        mPrompt = (mPrompt == null)? "" : mPrompt;

        setText(mPrompt);
        setOnClickListener(this);
    }

    public String getSelectedItem() {
        if (mSelection < 0 || mSelection >= MainActivity.mEntries.length) {
            return null;
        } else {
            return MainActivity.mEntries[mSelection].toString();
        }
    }

    public int getSelectedItemPosition() {
        return mSelection;
    }

    public void setSelection(int selection) {
        mSelection = selection;

        if (selection < 0) {
            setText(mPrompt);
        } else if (selection < MainActivity.mEntries.length) {
            setText(MainActivity.mEntries[mSelection]);
        }
    }

    public void setListener(ISpinnerTextViewListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(getContext())
                .setTitle(mPrompt)
                .setItems(MainActivity.mEntries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelection = which;
                        setText(MainActivity.mEntries[mSelection]);
                        if (mListener != null) {
                            mListener.onItemSelected(which);
                        }
                    }
                })
                .create().show();
    }
}