package alexey.com.facultativeapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import alexey.com.facultativeapp.Model.GitHubRepo;
import alexey.com.facultativeapp.R;

public class GitReposAdapter extends RecyclerView.Adapter<GitReposAdapter.RepoViewHolder> {

    /*
    Это класс, в котором определены все адаптеры для отрисовки списка
    репозиториев с гитхаб.
    Струтура данного класса повторяет структуру класса ContactListAdapter
     */

    private List<GitHubRepo> gitHubRepoList;

    public GitReposAdapter(List<GitHubRepo> gitHubRepoList) {
        this.gitHubRepoList = gitHubRepoList;
    }

    /*
    Это класс, описывающий один элемент списка репозиториев
     */

    public class RepoViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV;
        public RepoViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.itemRepoNameTV);
        }
    }


    /*
    Метод, для создания View по layout файлу.
    В данном случае принимает файл item_repo.
    Созданный View становится дочерним по отношению к RepoViewHolder
     */

    @NonNull
    @Override
    public RepoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repo, parent, false);
        return new RepoViewHolder(view);
    }

    /*
    Обновление одного элемента списка репозиториев
     */

    @Override
    public void onBindViewHolder(@NonNull RepoViewHolder holder, int position) {
        holder.nameTV.setText(gitHubRepoList.get(position).getName());
    }

    /*
    Подсчет длины списка репозиториев
     */

    @Override
    public int getItemCount() {
        if (gitHubRepoList != null) {
            return gitHubRepoList.size();
        } else {
            return 0;
        }
    }
}
