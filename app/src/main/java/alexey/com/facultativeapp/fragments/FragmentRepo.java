package alexey.com.facultativeapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alexey.com.facultativeapp.Model.GitHubRepo;
import alexey.com.facultativeapp.R;
import alexey.com.facultativeapp.adapters.GitReposAdapter;
import alexey.com.facultativeapp.helpers.App;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentRepo extends Fragment {

    /*
    Фрагмент, который предназначен для списка репозиториев гитхаб
     */
    private GitReposAdapter gitReposAdapter;
    private LinearLayoutManager layoutManager;
    private List<GitHubRepo> gitHubRepoList = new ArrayList<>();
    private RecyclerView gitHubReposRecyclerView;
    private String LOG = "LOG";

    /*
    Создание View по файлу fragment_repo
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo, container, false);
        gitHubReposRecyclerView = view.findViewById(R.id.gitHubReposRecyclerView);
        return view;
    }

    /*
    После создания activity назначаем layoutManager.
    И устанавливаем адаптер для репозиториев
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        gitHubReposRecyclerView.setLayoutManager(layoutManager);
        gitReposAdapter = new GitReposAdapter(gitHubRepoList);
        gitHubReposRecyclerView.setAdapter(gitReposAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadList();
    }

    /*
    Метод загрузки списка репозиториев
     */
    public void loadList() {
        App.getNetClient().getRepos(App.getUsername(), new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                /*
                Если запрос удачен,
                то чистим список репозиториев,
                добавляем туда то, что пришло в ответе
                и уведомляем адаптер, что произошло изменение в данных
                и ему нужно обновиться
                 */
                if (response.isSuccessful()) {
                    Log.d(LOG, "То что пришло = " + new Gson().toJson(response.body()));
                    gitHubRepoList.clear();
                    gitHubRepoList.addAll(response.body());
                    gitReposAdapter.notifyDataSetChanged();
                } else {
                    Log.d(LOG, "Код ошибки = " + response.code());
                    try {
                        Log.d(LOG, "Сообщение ошибки = " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
