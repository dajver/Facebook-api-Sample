package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.facebook_api_sample.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FriendsAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final String[] id;
	private final String[] images;
	private final String[] values;
	DisplayImageOptions options;

	public FriendsAdapter(Context runnable, String[] indexcode, String[] names, String[] urls) {

		super(runnable, R.layout.friendlist, names);
		context = runnable;
		values = names;
		images = urls;
		id = indexcode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.friendlist, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
		textView.setText(values[position]);
		//
		options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().bitmapConfig(
				Bitmap.Config.RGB_565).build();
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
		imageLoader.displayImage(images[position], imageView, options);
		return rowView;
	}
}
