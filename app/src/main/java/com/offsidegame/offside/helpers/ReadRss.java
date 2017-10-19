package com.offsidegame.offside.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.offsidegame.offside.adapters.NewsFeedAdapter;
import com.offsidegame.offside.models.FeedItem;
import com.offsidegame.offside.models.OffsideApplication;

import org.acra.ACRA;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.text.Html.FROM_HTML_MODE_COMPACT;


/**
 * Created by user on 10/3/2017.
 */

public class ReadRss extends AsyncTask<Void,Void, Void> {

    Context context;
    String address = "https://hazavit.co.il/feed/";
    //String address = "http://www.one.co.il/cat/coop/xml/rss/";
    //String address = "http://rss.cnn.com/rss/edition_sport.rss/";
    //String address = "http://rss.walla.co.il/?w=/3/0/12/@rss.e";


    URL url;
    //ProgressDialog progressDialog;
    ArrayList<FeedItem> feedItems;
    RecyclerView recyclerView;
    NewsFeedAdapter newsFeedAdapter;
    FrameLayout loadingProgress;

    public ReadRss(Context context, RecyclerView recyclerView, FrameLayout loadingProgress) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.loadingProgress = loadingProgress;
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Loading...");


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadingProgress.setVisibility(View.VISIBLE);
        //progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        loadingProgress.setVisibility(View.GONE);
        //progressDialog.dismiss();

        newsFeedAdapter = new NewsFeedAdapter(context,feedItems);
        recyclerView.setLayoutManager((new LinearLayoutManager(context)));
        recyclerView.addItemDecoration(new VerticalSpace(50));
        recyclerView.setAdapter(newsFeedAdapter);

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!recyclerView.canScrollVertically(1))
//                    onScrolledToBottom();
//            }
//        });
    }

//    private void onScrolledToBottom() {
//        if (top10feedItems.size() < feedItems.size()) {
//            int x, y;
//            if ((feedItems.size() - top10feedItems.size()) >= 10) {
//                x = top10feedItems.size();
//                y = x + 10;
//            } else {
//                x = top10feedItems.size();
//                y = x + feedItems.size() - top10feedItems.size();
//            }
//            for (int i = x; i < y; i++) {
//                top10feedItems.add(feedItems.get(i));
//            }
//            newsFeedAdapter.notifyDataSetChanged();
//        }
//
//    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            processXml(getData());

            return null;


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
            return null;

        }

    }

    private void processXml(Document data){
        String defaultNewsItemImage= OffsideApplication.getDefaultPictureUrlHazavitFeed();
        if(data != null){
            feedItems = new ArrayList<>();
            Element root = data.getDocumentElement();
            Node channel = null;
            for(int i=0;i<root.getChildNodes().getLength();i++){
                if(root.getChildNodes().item(i).getNodeName().equalsIgnoreCase("channel")){
                    channel = root.getChildNodes().item(i);
                    break;
                }
            }
            if(channel==null)
                return;

            NodeList items = channel.getChildNodes();
            for (int i=0;i<items.getLength();i++){
                Node currentChild = items.item(i);
                if(currentChild.getNodeName().equalsIgnoreCase("image")){
                    defaultNewsItemImage = currentChild.getChildNodes().item(1).getTextContent();
                }
                if(currentChild.getNodeName().equalsIgnoreCase("item")){
                    FeedItem item = new FeedItem();
                    NodeList itemChilds = currentChild.getChildNodes();
                    for(int j=0;j<itemChilds.getLength();j++){
                        Node currentNode = itemChilds.item(j);
                        if(currentNode.getNodeName().equalsIgnoreCase("title")){
                            item.setTitle(currentNode.getTextContent());
                        }
                        else if(currentNode.getNodeName().equalsIgnoreCase("description")){
                            String htmlCode = currentNode.getTextContent();
                            Spanned encodedDescription;
                            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                                encodedDescription = Html.fromHtml(htmlCode);
                            else
                                encodedDescription = Html.fromHtml(htmlCode,FROM_HTML_MODE_COMPACT);
                            item.setDescription(encodedDescription.toString());
                        }

                        else if(currentNode.getNodeName().equalsIgnoreCase("pubDate")){
                            item.setPubDate(currentNode.getTextContent());
                        }
                        else if(currentNode.getNodeName().equalsIgnoreCase("link")){
                            item.setLink(currentNode.getTextContent());
                        }
//                        else if(currentNode.getNodeName().equalsIgnoreCase("media:thumbnail")){
//                            //for the thumbnail url
//                            String url = currentChild.getAttributes().item(0).getTextContent();
//                            item.setThumbnailUri(url);
//                        }

                        else if(currentNode.getNodeName().equalsIgnoreCase("content:encoded")){
                            //for the thumbnail url
                            String htmlContent = currentNode.getTextContent();
                            String imageUrl = null;
                            try{
                               imageUrl = Jsoup.parse(htmlContent).getElementsByAttribute("src").get(0).attr("src");
                            }
                            catch (Exception ex){
                            }

                            item.setThumbnailUri(imageUrl);
                        }

                    }

                    if(item.getThumbnailUri()==null)
                        item.setThumbnailUri(defaultNewsItemImage);
                    //Log.d("thumbnail", item.getThumbnailUri());
                    feedItems.add(item);
                }
            }
        }
    }

    public Document getData(){
        try
        {
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc = builder.parse(inputStream);
            return xmlDoc;


        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return null;
        }
    }
}
