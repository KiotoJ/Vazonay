package com.example.vazonay;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class Mp3Adapter extends BaseAdapter implements ListAdapter, Filterable {
    private ArrayList<String> orglistraLohatenyMp3 = new ArrayList<String>();
    private ArrayList<String> listraLohatenyMp3 = new ArrayList<String>();
    private Context context;
    private MediaPlayerService player;
    boolean serviceBound = false;
    private ItemFilter mFilter = new ItemFilter();

    public Mp3Adapter(Context context, ArrayList<String> listraLohatenyMp3) {
        this.listraLohatenyMp3 = listraLohatenyMp3;
        this.orglistraLohatenyMp3 = listraLohatenyMp3;
        this.context = context;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<String> list = orglistraLohatenyMp3;

            if(constraint.toString() == null || constraint.toString().length() == 0){
                results.count = orglistraLohatenyMp3.size();
                results.values = orglistraLohatenyMp3;
            }else {
                int count = list.size();
                final ArrayList<String> nlist = new ArrayList<String>(count);

                String filterableString ;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listraLohatenyMp3 = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }

    @Override
    public int getCount() {
        return listraLohatenyMp3.size();
    }

    @Override
    public Object getItem(int position) {
        return listraLohatenyMp3.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(context, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_list_view, null);

            final TextView anaranaHiraIray = (TextView) view.findViewById(R.id.titra_text);
            final TextView infoHira = (TextView) view.findViewById(R.id.info_hira);
            anaranaHiraIray.setText(this.listraLohatenyMp3.get(position));
            infoHira.setText("jojhiuh");

            final ImageButton mozikaSary = (ImageButton) view.findViewById(R.id.alefa_hira);
            mozikaSary.setImageResource(R.drawable.mozika);
            anaranaHiraIray.setSelected(true);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}