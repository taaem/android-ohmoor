package io.taaem.vertretungsplan;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.lang.StringBuilder;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;

import io.taaem.vertretungsplan.R;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

import android.preference.PreferenceManager;


public class OneFragment extends Fragment{

    private RecyclerView recyclerView;
    private TextView emptyView;
    private SwipeRefreshLayout mRefreshLayout;
    private Boolean noItems;
    private MenuItem mInfoItem;
    // List of all Items
    ArrayList<singleItem> itemsList;

    RVAdapter adapter;
    SlideInBottomAnimationAdapter slideAdapter;

    public String infoMessage;
    // Final Target Url determinated at runtime
    private String targetEndpoint;

    // All Tags used in the JSON
    private static final String TAG_VERTRETER = "vertreter";
    private static final String TAG_LEHRER = "lehrer";
    private static final String TAG_KLASSE = "klasse";
    private static final String TAG_RAUM = "raum";
    private static final String TAG_STUNDE = "stunde";
    private static final String TAG_FACH = "fach";
    private static final String TAG_INFO = "info";


    // Public Constructor
    public OneFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemsList = new ArrayList<singleItem>();

        setHasOptionsMenu(true);
        // Initiate InfoMessage
        infoMessage = new String();
        // Get all arguments passed by the MainView
        Bundle args = getArguments();
        // Get the target Url
        String href = args.getString("href");

        // url konstrurctor
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String klasse = sharedPreferences.getString(SettingsActivity.KEY_PREF_USERKLASSE, "");
        String buchstabe = sharedPreferences.getString(SettingsActivity.KEY_PREF_USERBUCHSTABE, "");
        targetEndpoint = href + "/" + klasse + buchstabe;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });
        mRefreshLayout.post(new Runnable() {
            @Override public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        mInfoItem = (MenuItem)view.findViewById(R.id.info);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        if(noItems != null && noItems){
            emptyView.setVisibility(View.VISIBLE);
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new RVAdapter(itemsList);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        slideAdapter = new SlideInBottomAnimationAdapter(adapter);

        recyclerView.setAdapter(slideAdapter);
        recyclerView.setItemAnimator(new ScaleInAnimator());
//        recyclerView.getItemAnimator().setAddDuration(300);

        refreshItems();
        return view;
    }
    private void refreshItems()
    {
        new GetItems().execute();
    }
    // Everything for the Menu
    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (infoMessage.length() == 0) {
            menu.getItem(1).setVisible(false);
        }else{
            menu.getItem(1).setVisible(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // Show Message Of The day
            case R.id.info:
                Dialog iDialog = new Dialog(getContext());
                iDialog.setCancelable(true);
                iDialog.setContentView(R.layout.dialog_item);
                iDialog.setTitle("Info");
                TextView text = (TextView) iDialog.findViewById(R.id.dateInfo);
                text.setText(infoMessage);
                iDialog.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    class singleItem {
        String klasse;
        String vertreter;
        String raum;
        String info;
        String fach;
        String lehrer;
        String stunde;

        singleItem(String stunde,
                String klasse,
                String vertreter,
                String raum,
                String info,
                String fach,
                String lehrer) {
            this.klasse = klasse;
            this.vertreter = vertreter;
            this.raum = raum;
            this.info = info;
            this.fach = fach;
            this.lehrer = lehrer;
            this.stunde = stunde;
        }
    }
    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder>{

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView itemStunde;
            TextView itemKlasse;
            TextView itemVertreter;
            TextView itemLehrer;
            TextView itemRaum;
            TextView itemInfo;
            TextView itemFach;

            public ItemViewHolder(View itemView) {
                super(itemView);
                cv = (CardView)itemView.findViewById(R.id.card_view);
                itemStunde = (TextView)itemView.findViewById(R.id.stunde);
                itemKlasse = (TextView)itemView.findViewById(R.id.klasse);
                itemRaum = (TextView)itemView.findViewById(R.id.raum);
                itemInfo = (TextView)itemView.findViewById(R.id.itemInfo);
                itemLehrer = (TextView)itemView.findViewById(R.id.lehrer);
                itemVertreter = (TextView)itemView.findViewById(R.id.vertreter);
                itemFach = (TextView)itemView.findViewById(R.id.fach);

            }
        }

        ArrayList<singleItem> items;
        RVAdapter(ArrayList<singleItem> items){
            this.items = items;
        }
        public void addItemsToList(singleItem item){
            this.items.add(item);
        }
        public void removeAllItems(){
            items.clear();
        }
        @Override
        public int getItemCount() {
            return items.size();
        }
        @Override
         public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();

            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.list_item, viewGroup, false);
            ItemViewHolder ivh = new ItemViewHolder(v);
            return ivh;
        }
        @Override
        public void onBindViewHolder(ItemViewHolder personViewHolder, int i) {
            personViewHolder.itemStunde.setText(items.get(i).stunde);
            personViewHolder.itemKlasse.setText(items.get(i).klasse);
            personViewHolder.itemFach.setText(items.get(i).fach);
            personViewHolder.itemLehrer.setText(items.get(i).lehrer);
            personViewHolder.itemVertreter.setText(items.get(i).vertreter);
            personViewHolder.itemRaum.setText(items.get(i).raum);
            personViewHolder.itemInfo.setText(items.get(i).info);
        }


    }

    private class GetItems extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String mUrl = ApiInfo.getUrl() + "/plan/" + targetEndpoint;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(mUrl, ApiInfo.getKey());

            Log.i("URL", mUrl);

            if (jsonStr != null) {
                publishProgress(jsonStr);
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }
        @Override
        protected void onProgressUpdate(String... jsonStr) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr[0]);

                JSONArray infoMessageArr = jsonObj.getJSONArray("msg");
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < infoMessageArr.length(); i++){
                    if (stringBuilder.length() > 0){
                        stringBuilder.append("\n");
                    }
                    stringBuilder.append(infoMessageArr.getString(i));
                }

                infoMessage = stringBuilder.toString();
                if (infoMessage.length() != 0){
                    getActivity().invalidateOptionsMenu();
                }

                JSONArray jsonArr = jsonObj.getJSONArray("items");
                if (jsonArr.length() == 0){
                    noItems = true;
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setNestedScrollingEnabled(false);
                }else {
                    noItems = false;
                    // Deleting all previous Items
                    adapter.removeAllItems();
                    adapter.notifyDataSetChanged();
                    slideAdapter.notifyDataSetChanged();
                    // looping through All Items
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject c = jsonArr.getJSONObject(i);

                        String lehrer = c.getString(TAG_LEHRER);
                        String fach = c.getString(TAG_FACH);
                        String info = c.getString(TAG_INFO);
                        String klasse = c.getString(TAG_KLASSE);
                        String raum = c.getString(TAG_RAUM);
                        String stunde = c.getString(TAG_STUNDE);
                        String vertreter = c.getString(TAG_VERTRETER);

                        // tmp hashmap for single contact
                        singleItem item = new singleItem(
                                stunde, klasse, vertreter, raum, info, fach, lehrer
                        );

                        adapter.addItemsToList(item);
                        slideAdapter.notifyItemInserted(adapter.getItemCount() - 1);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            /**
             * Updating parsed JSON data into ListView
             * */
            mRefreshLayout.setRefreshing(false);
            //adapter.notifyDataSetChanged();

        }
    }


}