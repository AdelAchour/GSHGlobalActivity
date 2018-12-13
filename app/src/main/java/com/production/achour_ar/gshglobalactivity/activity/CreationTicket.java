package com.production.achour_ar.gshglobalactivity.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.production.achour_ar.gshglobalactivity.data_model.KeyValuePair;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.data_model.CategorieTicketModel;
import com.production.achour_ar.gshglobalactivity.data_model.Constants;
import com.production.achour_ar.gshglobalactivity.manager.URLGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.production.achour_ar.gshglobalactivity.activity.AccueilUser.generateUrl;

public class CreationTicket extends AppCompatActivity {

    EditText titreTV, catTV, typeTV, contentTV;
    Button createTicketBtn;
    String session_token, nameUser, idUser, firstnameUser;
    RequestQueue queue;
    String nbCountCategorie, nameCat, completenamCat, idCat;
    ArrayList<CategorieTicketModel> listCategorieTicket;
    private String idCatRequest;
    private int idTypeRequest;
    ProgressDialog pd;
    public static Handler handlerCreation;
    ProgressDialog pdCreation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_ticket);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Créer un nouveau ticket");
        actionBar.setHomeButtonEnabled(true); //show a caret only if android:parentActivityName is specified.


       //Mapping
        titreTV = findViewById(R.id.titreNewTicket);
        catTV = findViewById(R.id.categorieNewTicket);
        typeTV = findViewById(R.id.typeNewTicket);
        contentTV = findViewById(R.id.descriptionNewTicket);

        createTicketBtn = findViewById(R.id.createTicketButton);

        catTV.setFocusable(false);
        catTV.setClickable(true);

        typeTV.setFocusable(false);
        typeTV.setClickable(true);


        //Récupération des informations
        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        //Initializations
        handlerCreation = new HandlerCreation();
        queue = Volley.newRequestQueue(getApplicationContext());
        listCategorieTicket = new ArrayList<CategorieTicketModel>();
        pdCreation = new ProgressDialog(CreationTicket.this);
        pdCreation.setMessage("Création du ticket...");

        pd = new ProgressDialog(CreationTicket.this);
        pd.setMessage("Chargement du formulaire...");
        pd.show();


        PopulateCategorieTicket();
        PopulateTypeTicket();

        createTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titreTicket = titreTV.getText().toString().trim();
                String catTicket = catTV.getText().toString().trim();
                String typeTicket = typeTV.getText().toString().trim();
                String contentTicket = contentTV.getText().toString().trim();

                if ((titreTicket.equals(""))||(contentTicket.equals(""))||(catTicket.equals(""))||(typeTicket.equals(""))){

                    if (titreTicket.equals("")){
                        titreTV.setError("Le titre est obligatoire");
                        titreTV.requestFocus();
                    }
                    if (contentTicket.equals("")){
                        contentTV.setError("La description est obligatoire");
                        contentTV.requestFocus();
                    }
                    if (catTicket.equals("")){
                        catTV.setError("La catégorie est obligatoire");
                        catTV.requestFocus();
                    }
                    if (typeTicket.equals("")){
                        typeTV.setError("Le type est obligatoire");
                        typeTV.requestFocus();
                    }
                }
                else{
                    pdCreation.show();
                    CreateTicketHTTP(titreTicket, idCatRequest, idTypeRequest, contentTicket);
                    //System.out.println("Titre bro = \n"+titreTicket);
                    //System.out.println("Content bro = \n"+contentTicket.replaceAll("\n", "\\\\r\\\\n"));
                }

            }
        });


    }

    private void PopulateTypeTicket() {
        typeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(CreationTicket.this);
                builderSingle.setTitle("Sélectionnez le type");


                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CreationTicket.this,
                        android.R.layout.select_dialog_singlechoice);

                arrayAdapter.add("Demande");
                arrayAdapter.add("Incident");


                builderSingle.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);

                        switch (strName){
                            case "Demande":
                                idTypeRequest = 2;
                                System.out.println("idType = "+idTypeRequest);
                                break;

                            case "Incident":
                                idTypeRequest = 1;
                                System.out.println("idType = "+idTypeRequest);
                                break;
                        }

                        typeTV.setText(strName);

                    }
                });
                builderSingle.show();
            }
        });
    }

    private void CreateTicketHTTP(final String titreTicket, final String idCatRequest, final int idTypeRequest, final String contentTicket) {
        System.out.println("titre: "+titreTicket);
        System.out.println("id: "+idCatRequest);
        System.out.println("content: "+contentTicket);

        String url = Constants.GLPI_URL+"Ticket/";


        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Do something with response
                        handlerCreation.sendEmptyMessage(1);
                        Toast.makeText(getApplicationContext(), "Ticket créé !", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response!", error.toString());
                        handlerCreation.sendEmptyMessage(1);
                        Toast.makeText(getApplicationContext(), "Création impossible", Toast.LENGTH_SHORT).show();
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

            @Override
            public byte[] getBody() {

                String b = contentTicket.replaceAll("\"","\\\\\"");
                b = b.replaceAll("\n", "\\\\n");
                b = b.replaceAll("\r", "\\\\r");

                String Json_Payload = "{\"input\": {\"name\": \""+titreTicket+"\",\t\"content\": \""+b+"\",\"itilcategories_id\": \""+idCatRequest+"\",\"type\": \""+idTypeRequest+"\"}}"; // put your json
                return Json_Payload.getBytes();
            }

        };

        // add it to the RequestQueue
        queue.add(getRequest);

    }


    private void PopulateCategorieTicket() {
        String url = Constants.GLPI_URL+"search/ITILCategory";

        int maxRange = 50;
        List<KeyValuePair> params = new ArrayList<>();

        //AFFICHAGE
        params.add(new KeyValuePair("forcedisplay[0]","1"));
        params.add(new KeyValuePair("forcedisplay[1]","2"));
        params.add(new KeyValuePair("forcedisplay[2]","14"));

        //ORDRE ET RANGE
        params.add(new KeyValuePair("sort","2"));
        params.add(new KeyValuePair("range","0-"+maxRange+""));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            nbCountCategorie = response.getString("totalcount");
                            System.out.println("nb catégorie = "+nbCountCategorie);

                            JSONArray Jdata = response.getJSONArray("data");
                            for (int i=0; i < Jdata.length(); i++) {
                                try {
                                    JSONObject oneCat = Jdata.getJSONObject(i);

                                    nameCat = oneCat.getString("14");
                                    completenamCat = oneCat.getString("1");
                                    idCat = oneCat.getString("2");

                                } catch (JSONException e) {
                                    Log.e("Nb of data: "+Jdata.length()+" || "+"Error JSONArray at "+i+" : ", e.getMessage());
                                }
                                // ------------------------

                                /* Remplissage de la liste pour les catégories */
                                listCategorieTicket.add(new CategorieTicketModel(nameCat,completenamCat,idCat));

                                // -----------------------------

                            }

                            handlerCreation.sendEmptyMessage(0);
                            catTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(CreationTicket.this);
                                    builderSingle.setTitle("Sélectionnez une catégorie");


                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CreationTicket.this,
                                            android.R.layout.select_dialog_singlechoice);
                                    for (int i = 0; i<listCategorieTicket.size(); i++){
                                        CategorieTicketModel catTicketModel = (CategorieTicketModel)listCategorieTicket.get(i);
                                        arrayAdapter.add(catTicketModel.getName());
                                    }

                                    builderSingle.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            CategorieTicketModel catTicketModel = (CategorieTicketModel)listCategorieTicket.get(which);

                                            catTV.setText(strName);
                                            idCatRequest = catTicketModel.getId();
                                            System.out.println("idCat = "+idCatRequest);
                                        }
                                    });
                                    builderSingle.show();

                                }
                            });



                        } catch (JSONException e) {
                            Log.e("malkach",e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response!", error.toString());
                        Toast.makeText(getApplicationContext(), "Vérifiez votre connexion", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class HandlerCreation extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if (pd.isShowing()){
                        pd.dismiss();
                    }
                    break;

                case 1:
                    if(pdCreation.isShowing()){
                        pdCreation.dismiss();
                    }
                    break;
            }

        }
    }

}
