package raimundasindriulis.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecycleAdapter Adapter;
    private RecyclerView RecycleLayout;
    private TextView error;
    private TextView pageTxt;
    CardView pageCard;
    StaggeredGridLayoutManager mStaggeredLayoutManager;
    private ProgressDialog mProgressDialog;
    private List<User> items;
    int page = 1;
    int lastPage;
    String pageStr;

    String ROOT_URL = "http://api.ieskok.lt/online.php?p=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        pageStr = getResources().getString(R.string.page);
        error = (TextView) findViewById(R.id.error);
        pageTxt = (TextView) findViewById(R.id.page);
        pageCard = (CardView) findViewById(R.id.pageCard);
        RecycleLayout = (RecyclerView) findViewById(R.id.recycler);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        RecycleLayout.setItemAnimator(new DefaultItemAnimator());
        RecycleLayout.setLayoutManager(mStaggeredLayoutManager);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_grid:
                if(mStaggeredLayoutManager.getSpanCount() == 1)
                {
                    mStaggeredLayoutManager.setSpanCount(2);
                    item.setIcon(R.drawable.list_icon);
                }
                else {
                    mStaggeredLayoutManager.setSpanCount(1);
                    item.setIcon(R.drawable.grid_icon);
                }
                return true;
            case R.id.action_refresh:
                init();
                return true;
            case R.id.action_next:
                if(page!=lastPage){
                    ++page;
                    if(page>1){
                        pageCard.animate().scaleX(1).scaleY(1);
                        pageTxt.setText(pageStr + page);
                    }
                    init();
                }

                return true;
            case R.id.action_prev:
                if(page>1){
                    --page;
                    init();
                    pageTxt.setText(pageStr + page);
                    if(page==1){
                        pageCard.animate().scaleX(0).scaleY(0);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    private void init(){
        showProgressDialog();
        Ion.with(getBaseContext())
                .load(ROOT_URL + page)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result!=null){
                            JsonArray usersList = result.get("users_list").getAsJsonArray();
                            page = result.get("page").getAsInt();
                            lastPage = result.get("last_page").getAsInt();
                            List<User> users = new ArrayList<>();
                            for (int i = 0; i < usersList.size(); i++){
                                User newUser = new User();
                                newUser.id = usersList.get(i).getAsJsonObject().get("id").getAsInt();
                                newUser.vardas = usersList.get(i).getAsJsonObject().get("vardas").getAsString();
                                newUser.loco = usersList.get(i).getAsJsonObject().get("loco").getAsString();
                                newUser.age = usersList.get(i).getAsJsonObject().get("age").getAsString();
                                users.add(newUser);
                            }
                            error.setVisibility(View.GONE);
                            items = users;
                            setCache();
                        } else {
                            error.setVisibility(View.VISIBLE);
                            items = ItemORM.getItems(getBaseContext());
                        }
                        if(items!=null){
                            UpdateList();
                        }
                        hideProgressDialog();
                    }
                });
    }

    void UpdateList(){
        if (Adapter==null){
            Adapter = new RecycleAdapter(items, R.layout.recycler_row);
        }
        if(RecycleLayout.getAdapter()==null){
            RecycleLayout.setAdapter(Adapter);
        } else {
            Adapter.swap(items);
        }
    }

    public void detailActivity(View v, String name, String age, String loco, int id, int f){
        Intent intent;
        intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra("vardas", name);
        intent.putExtra("age", age);
        intent.putExtra("loco", loco);
        intent.putExtra("id", id);
        intent.putExtra("f", f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageView image = (ImageView) v.findViewById(R.id.image);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, image, "trImage");
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }

    private void setCache(){
        ItemORM.dropTable(getBaseContext());
        if (items.size()>0){
            for(int i = 0; i < items.size(); i++){
                ItemORM.insertItem(getBaseContext(), items.get(i));
            }
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
