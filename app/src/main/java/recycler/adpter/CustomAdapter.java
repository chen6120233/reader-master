package recycler.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feng.freader.R;
import com.feng.freader.entity.ReadEntity;
import com.feng.freader.view.activity.item.AddWebActivity;
import com.feng.freader.view.activity.item.CustomActivity;
import com.feng.freader.view.activity.item.Web2Activity;
import com.feng.freader.view.activity.item.WebActivity;

import java.util.List;

import recycler.CommonAdapter;
import recycler.base.ViewHolder;

public class CustomAdapter extends CommonAdapter<ReadEntity> {

    public CustomAdapter(Context context, int layoutId, List<ReadEntity> datas) {
        super(context,layoutId,datas);
    }


    @Override
    protected void convert(final ViewHolder holder, final ReadEntity readEntity, int position) {
        LinearLayout linear = holder.getView(R.id.linear);
        linear.setVisibility(View.VISIBLE);
        TextView tv_item_novel_title = holder.getView(R.id.tv_item_novel_title);
        TextView tv_item_novel_short_info = holder.getView(R.id.tv_item_novel_short_info);
        tv_item_novel_title.setText(readEntity.getName());
        tv_item_novel_short_info.setText("");

        Button bianji = holder.getView(R.id.bianji);
        Button web2 = holder.getView(R.id.web2);
        bianji.setVisibility(View.VISIBLE);
        web2.setVisibility(View.VISIBLE);
        web2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomActivity.URL = readEntity;
                mContext. startActivity(new Intent(mContext,Web2Activity.class));
            }
        });
        bianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(mContext, AddWebActivity.class);
                intent.putExtra("item",readEntity);
                ((Activity)mContext).startActivity(intent);
            }
        });

    }
}

