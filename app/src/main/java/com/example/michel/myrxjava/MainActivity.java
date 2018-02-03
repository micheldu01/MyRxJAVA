package com.example.michel.myrxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {


    private Disposable disposable;
    private TextView textView;
    private RecyclerView recyclerView;
    //declare list of user (GithubUser) & Adapter
    //private List<GithubUser> githubUsers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.main_text);



        //call the stream
        this.recuperationHTTP();

        //call the stream topStories
        this.getTopStoriesHTTP();



    }

    // execute our stream
    private void getTopStoriesHTTP(){
        //during search
        this.textView.setText("Downloading...");
        // execute the stream subscribing to observable defined inside NytStream
        this.disposable = NytStreams.streamTopStories()
                .subscribeWith(new DisposableObserver<NYresult>() {
                    @Override
                    public void onNext(NYresult nYresult) {
                        textView.setText(nYresult.getResults().get(0).getTitle());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 1 - Execute our Stream
    private void recuperationHTTP(){
        // 1.1 - Update UI
        this.textView.setText("Downloading...");
        // 1.2 - Execute the stream subscribing to Observable defined inside GithubStream
        this.disposable = GithubStreams.streamSuiveur("JakeWharton")
                .subscribeWith(new DisposableObserver<List<GithubUser>>() {


            @Override
            public void onNext(List<GithubUser> users) {

                // 1.3 - Update UI with list of users
                StringBuilder stringBuilder = new StringBuilder();
                for (GithubUser user : users){
                    stringBuilder.append("-"+user.getLogin()+"\n");
                }
                textView.setText(stringBuilder.toString());
            }


            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Log.e("TAG","On Complete !!");
            }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    public void onDestroy(){
        super.onDestroy();
        this.disposeWhenDestroy();
    }
}