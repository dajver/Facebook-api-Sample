package adapter;

import objects.UserFB;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.facebook_api_sample.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class FriendsAdapter extends ArrayAdapter<UserFB> { // унаследовали от нашего класса UserFB

        private final Activity activity;
        //
        private final ArrayList<UserFB> entries;

        // конструктор класса, принимает активность, листвью и массив данных
        public FriendsAdapter(final Activity a, final int textViewResourceId, final ArrayList<UserFB> entries) {

                super(a, textViewResourceId, entries);
                this.entries = entries;
                activity = a;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                View v = convertView;
                ViewHolder holder;
                if (v == null) {
                        LayoutInflater inflater = (LayoutInflater) activity
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        v = inflater.inflate(R.layout.friendlist, parent, false);
                        holder = new ViewHolder();
                        // инициализируем нашу разметку
                        holder.textView = (TextView) v.findViewById(R.id.label);
                        holder.imageView = (ImageView) v.findViewById(R.id.logo);
                        v.setTag(holder);
                } else {
                        holder = (ViewHolder) v.getTag();
                }
                UserFB userFB = entries.get(position);
                if (userFB != null) {
                        // получаем текст из массива
                        holder.textView.setText(userFB.getName());
                        // скачиваем картинки
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
                        imageLoader.displayImage(userFB.getUrl(), holder.imageView);
                }
                return v;
        }

        // для быстроты вынесли в отдельный класс
        private static class ViewHolder {

                public ImageView imageView;
                public TextView textView;
        }
}