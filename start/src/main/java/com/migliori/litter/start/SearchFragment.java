package com.migliori.litter.start;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Created by Anthony on 2/22/14.
 */

public class SearchFragment extends Fragment{

    Query query;
    Button searchButton;
    QueryResult result;
    EditText search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<Card> cards = new ArrayList<Card>();
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        search = (EditText) getActivity().findViewById(R.id.searchText);
        search.requestFocus();
        searchButton = (Button) getActivity().findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

                initSearch();

                }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    initSearch();
                }
                return handled;
            }
        });
    }


    private void initSearch() {

        ArrayList<Card> cards = new ArrayList<Card>();
        Twitter twitter = TwitterFactory.getSingleton();
        List<Status> statuses = null;
        try {
                if(search.getText().toString() != null) {
                    query = new Query(search.getText().toString());
                    result = twitter.search(query);
                    // statuses = twitter.getHomeTimeline();
                    statuses = result.getTweets();
                    Toast.makeText(getActivity().getApplicationContext(), "Search Started", Toast.LENGTH_SHORT).show();

                }
                   else
                    Toast.makeText(getActivity().getApplicationContext(), "No Input Detected", Toast.LENGTH_SHORT).show();


        } catch (TwitterException e) { e.printStackTrace(); }
        if(statuses != null) {
            for (Status status : statuses) {
                CustomCard card = new CustomCard(getActivity(), status.getUser().getName(), status.getText());
                Card links = new Card(getActivity());
                links.setTitle("links go here");
                CardThumbnail thumb = new CardThumbnail(getActivity());
                thumb.setUrlResource(status.getUser().getBiggerProfileImageURL());
                card.addCardThumbnail(thumb);
                card.setSwipeable(true);
                cards.add(card);
            }
        }
        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);

        CardListView listView = (CardListView) getActivity().findViewById(R.id.card_search);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }
    }


    public class CustomCard extends Card {

        protected String mTitleHeader;
        protected String mTitleMain;

        public CustomCard(Context context, String titleHeader, String titleMain) {
            super(context, R.layout.carddemo_example_inner_content);
            this.mTitleHeader = titleHeader;
            this.mTitleMain = titleMain;
            init();
        }

        private void init() {

            //Create a CardHeader
            CardHeader header = new CardHeader(getActivity());

            //Set the header title
            header.setTitle(mTitleHeader);
            addCardHeader(header);

            //Add ClickListener
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getContext(), "Click Listener card=" + mTitleHeader, Toast.LENGTH_SHORT).show();
                }
            });

            //Set the card inner text
            setTitle(mTitleMain);
        }
    }

}
