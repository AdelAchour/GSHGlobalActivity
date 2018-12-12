package com.production.achour_ar.gshglobalactivity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.adapter.TicketSearchAdapter;
import com.production.achour_ar.gshglobalactivity.adapter.TicketSearchModel;
import com.production.achour_ar.gshglobalactivity.data_model.Constants;
import com.production.achour_ar.gshglobalactivity.data_model.KeyValuePair;
import com.production.achour_ar.gshglobalactivity.manager.URLGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.production.achour_ar.gshglobalactivity.activity.AccueilUser.generateUrl;

public class SearchTicket extends AppCompatActivity {

    ArrayList<TicketSearchModel> TicketSearchModels;
    EditText searchET;
    ListView listViewSearch;
    private static TicketSearchAdapter adapter;
    String session_token, idUser, nameUser, firstnameUser;
    RequestQueue queue;
    String titreTicket, dateDebutTicket, idTicket, statutTicket;
    String nbCount;
    ProgressDialog pd ;
    public static Handler handlerSearchTicket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_ticket);

        //ActionBar Work
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Recherche de ticket");
        actionBar.setHomeButtonEnabled(true); //show a caret only if android:parentActivityName is specified.


        //Récupération
        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        //Initialization
        queue = Volley.newRequestQueue(this);

        handlerSearchTicket = new HandlerTicketSearch();

        pd = new ProgressDialog(this);
        pd.setMessage("Recherche des tickets...");

        listViewSearch=(ListView)findViewById(R.id.listSearchTicket);

        TicketSearchModels = new ArrayList<>();

        searchET = (EditText)findViewById(R.id.searchTicketET);

        //SEARCH
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TicketSearchModels.isEmpty()){ //if pleins
                        TicketSearchModels.clear();
                    }
                    pd.show();
                    SearchThenPolulate(searchET.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });


    }

    private void SearchThenPolulate(String title) {
        String url = Constants.GLPI_URL+"search/Ticket";

        List<KeyValuePair> params = new ArrayList<>();
        params.add(new KeyValuePair("criteria[0][field]","5"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",idUser));

        params.add(new KeyValuePair("criteria[1][link]","AND"));
        params.add(new KeyValuePair("criteria[1][field]","1"));
        params.add(new KeyValuePair("criteria[1][searchtype]","contains"));
        params.add(new KeyValuePair("criteria[1][value]",title));

        params.add(new KeyValuePair("forcedisplay[0]","15"));
        params.add(new KeyValuePair("forcedisplay[1]","12"));

        params.add(new KeyValuePair("range","0-200"));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            nbCount = response.getString("count");

                            JSONArray Jdata = response.getJSONArray("data");
                            for (int i=0; i < Jdata.length(); i++) {
                                try {
                                    JSONObject oneTicket = Jdata.getJSONObject(i);
                                    // Récupération des items pour le row_item
                                    titreTicket = oneTicket.getString("1");
                                    dateDebutTicket = oneTicket.getString("15");
                                    idTicket = oneTicket.getString("2");
                                    statutTicket = oneTicket.getString("12");

                                } catch (JSONException e) {
                                    Log.e("Nb of data: "+Jdata.length()+" || "+"Error JSONArray at "+i+" : ", e.getMessage());
                                }
                                // ------------------------

                                /* Remplissage du tableau des tickets pour le row item */
                                Log.d("STATUT", statutTicket);
                                TicketSearchModels.add(new TicketSearchModel(titreTicket,dateDebutTicket,idTicket,statutTicket));
                     
                            }


                            adapter = new TicketSearchAdapter(TicketSearchModels, getApplicationContext());
                            
                            listViewSearch.setAdapter(adapter);
                            handlerSearchTicket.sendEmptyMessage(1);



                            listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    TicketSearchModel TicketSearchModel = TicketSearchModels.get(position);

                                    /*Snackbar.make(view, "Id = "+TicketSearchModel.getIdTicket(), Snackbar.LENGTH_LONG)
                                            .setAction("No action", null).show();*/


                                    if (TicketSearchModel.getStatutTicket().equals("6")){ //clos
                                        Intent i = new Intent(getApplicationContext(), InfoTicketClos.class);
                                        i.putExtra("session",session_token);
                                        i.putExtra("nom",nameUser);
                                        i.putExtra("prenom",firstnameUser);
                                        i.putExtra("id",idUser);
                                        i.putExtra("idTicket",TicketSearchModel.getIdTicket());
                                        startActivity(i);
                                    }
                                    else { //non clos
                                        Intent i = new Intent(getApplicationContext(), InfoTicket.class);
                                        i.putExtra("session",session_token);
                                        i.putExtra("nom",nameUser);
                                        i.putExtra("prenom",firstnameUser);
                                        i.putExtra("id",idUser);
                                        i.putExtra("idTicket",TicketSearchModel.getIdTicket());
                                        startActivity(i);
                                    }

                                }
                            });


                        } catch (JSONException e) {
                            Log.e("malkach",e.getMessage());
                            handlerSearchTicket.sendEmptyMessage(2);
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Vérifiez votre connexion", Toast.LENGTH_LONG).show();
                        Log.e("Error.Response", error.toString());
                    }

                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("App-Token",Constants.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }

        };

        // add it to the RequestQueue
        queue.add(getRequest);
    }


    class HandlerTicketSearch extends Handler {
        boolean nodata = false;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    System.out.println("Je dois arrêter le chargement recherche");
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                    else {
                        System.out.println("Aucun chargement à arrêter recherche");
                    }
                    break;

                case 2:
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                    Snackbar.make(findViewById(android.R.id.content), "Aucun résultat trouvé", Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                    break;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
