package com.hira.antsivaskoto;

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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class Mp3Adapter extends BaseAdapter implements ListAdapter, Filterable {
    private ArrayList<String> orglistraLohatenyMp3 = new ArrayList<>();
    private ArrayList<String> listraLohatenyMp3 = new ArrayList<>();
    private final Context context;
    boolean serviceBound = false;
    private final ItemFilter mFilter = new ItemFilter();
    private static final int TYPE_ITEM = 0;

    private static final int TYPE_SUBITEM = 1;

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
                final ArrayList<String> nlist = new ArrayList<>(count);

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
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            MediaPlayerService player = binder.getService();
            serviceBound = true;

            Toast.makeText(context, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }


    };

    // for populating listview once use
    public static class MyHolder {
        final TextView anaranaHiraIray;
        final TextView infoHira;
        final ImageView saryAmHira;
        public MyHolder(View view) {
            anaranaHiraIray = view.findViewById(R.id.titra_text);
            infoHira = view.findViewById(R.id.info_hira);
            //titraTextLaharana = (TextView) view.findViewById(R.id.titra_text_laharana);
            saryAmHira = view.findViewById(R.id.sary_am_hira);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MyHolder holder;

        String[] anaranaFichierSplitted = this.listraLohatenyMp3.get(position).split("\\.");
        String titraTextLaharanaSplitted = anaranaFichierSplitted[0];
        String titraHiraSplitted = anaranaFichierSplitted[1];
        String infoHiraSplitted = anaranaFichierSplitted[2];

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_list_view, null);

            holder = new MyHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (MyHolder) view.getTag();
        }

        String PACKAGE_NAME = holder.saryAmHira.getContext().getPackageName();
        //holder.titraTextLaharana.setText(titraTextLaharanaSplitted);
        int imgId = holder.saryAmHira.getContext().getResources().getIdentifier(PACKAGE_NAME+":drawable/_"+titraTextLaharanaSplitted , null, null);
        holder.anaranaHiraIray.setText(titraHiraSplitted);
        holder.infoHira.setText(infoHiraSplitted);
        holder.saryAmHira.setImageResource(imgId);
        holder.anaranaHiraIray.setSelected(true);
        holder.infoHira.setSelected(true);
        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}