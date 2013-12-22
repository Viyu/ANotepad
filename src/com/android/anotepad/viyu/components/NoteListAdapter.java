package com.android.anotepad.viyu.components;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.anotepad.viyu.R;
import com.android.anotepad.viyu.data.NoteObject;
import com.android.anotepad.viyu.utility.Constant;

public class NoteListAdapter extends BaseAdapter {
    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;

    private List<? extends Map<String, ?>> mData;

    private int mResource;
    private int mDropDownResource;
    private LayoutInflater mInflater;
    
    private Context context = null;
   
    public NoteListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
    	mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

   
    public long getItemId(int position) {
        return position;
    }

   
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    @SuppressWarnings("rawtypes")
	private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = mViewBinder;
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;
        
        int color = view.getResources().getColor(R.color.white);
        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }
                if(data instanceof NoteObject<?, ?>) {
                	color = view.getResources().getColor(((NoteObject)data).getTextColor().value());
                }
                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() + " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                    	//Add sequence number
                    	if(from[i].equals(Constant.NOTE_LIST_COLUMN_CONTENT)) {
                    		text = (position + 1) + ". " + text;
                    	}
                    	//Custom the last modified time format
                    	if(from[i].equals(Constant.NOTE_LIST_COLUMN_LASTMODIFIED)) {
                    		Date lastModified = new Date(Long.parseLong(text));
                    		String lastModifiedDay = DateFormat.getDateFormat(context).format(lastModified); 
                    		Date currentTime = new Date(System.currentTimeMillis());
                    		if(lastModifiedDay.equals(DateFormat.getDateFormat(context).format(currentTime))) {
                    			text = DateFormat.getTimeFormat(context).format(lastModified);
                    		} else {
                    			text = lastModifiedDay;
                    		}
                    	}
                    	((TextView) v).setTextColor(color);
                    	text += "";
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);                            
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " + " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    
    public ViewBinder getViewBinder() {
        return mViewBinder;
    }

 
    public void setViewBinder(ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

   
    public void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }

  
    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }

    public void setViewText(TextView v, String text) {
        v.setText(text);
    }

    public static interface ViewBinder {
      
        boolean setViewValue(View view, Object data, String textRepresentation);
    }

}
