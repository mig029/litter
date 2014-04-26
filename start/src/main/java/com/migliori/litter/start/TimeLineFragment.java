package com.migliori.litter.start;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Created by Anthony on 2/23/14.
 */
public class TimeLineFragment extends Fragment{

    Query query;
    QueryResult result;
    Button timeLineButton;
    Button tweet;
    String updateStatus;
    public EditText statusText;
    Paging paging = new Paging(1, 20);
    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;



    ArrayList<String> setTimeLine;
    ListView timeline;
    ImageView profilePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
       View view = inflater.inflate(R.layout.fragment_timeline, container, false);



    return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        initCards();

    }


    private void initCards() {

        ArrayList<Card> cards = new ArrayList<Card>();
        final Twitter twitter = TwitterFactory.getSingleton();

        List<Status> statuses = null;
        try {
            statuses = twitter.getHomeTimeline();
            twitter.getHomeTimeline();
        } catch (TwitterException e) { e.printStackTrace(); }

        for (final Status status : statuses) {

            CardExample card = new CardExample(getActivity(),status.getUser().getName(),status.getText());
            CardHeader header = new CardHeader(getActivity());
            header.setButtonOverflowVisible(true);
            //Add a popup menu. This method set OverFlow button to visibile
            header.setPopupMenu(R.menu.popupmain, new CardHeader.OnClickCardHeaderPopupMenuListener(){
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {

                    final Status stat = status;

                    final Twitter twit = twitter;
                    if(item.getTitle() == "@Reply")
                    {
                        atReply(stat, twit);
                    }
                     //Toast.makeText(getActivity(), "Click on "+item.getTitle(), Toast.LENGTH_SHORT).show();

                    }
            });

            //Add a PopupMenuPrepareListener to add dynamically a menu entry
            //It is optional.
            header.setPopupMenuPrepareListener(new CardHeader.OnPrepareCardHeaderPopupMenuListener() {
                @Override
                public boolean onPreparePopupMenu(BaseCard card, PopupMenu popupMenu) {
                    popupMenu.getMenu().add("@Reply");
                    popupMenu.getMenu().add("Follow");
                    return true;

                /*
                 * Other examples:
                 * You can remove an item with this code:
                 * popupMenu.getMenu().removeItem(R.id.action_settings);
                 *
                 * You can use return false to hide the button and the popup
                 * return false;
                 */

                }
            });
            CardThumbnail thumb = new CardThumbnail(getActivity());
            thumb.setUrlResource(status.getUser().getBiggerProfileImageURL());
            card.addCardHeader(header);
            card.addCardThumbnail(thumb);
            card.setSwipeable(true);
            cards.add(card);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);

        CardListView listView = (CardListView) getActivity().findViewById(R.id.carddemo_list);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }
    }

    public class CardExample extends Card {

        protected String mTitleHeader;
        protected String mTitleMain;

        public CardExample(Context context, String titleHeader, String titleMain) {
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
                    card.getCardExpand();
                    Toast.makeText(getContext(), "Click Listener card=" + mTitleHeader, Toast.LENGTH_SHORT).show();
                }
            });

            //Set the card inner text
            setTitle(mTitleMain);
        }
    }

    public void atReply(final Status status, final Twitter twitter){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Reply to ");
        alert.setMessage("@" + status.getUser().getName());

        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                status.getId();
                status.getUser().getScreenName();

                StatusUpdate st = new StatusUpdate("@" + status.getUser().getScreenName() + " " + input.getText().toString());
                st.inReplyToStatusId(status.getId());

                try {
                       twitter.updateStatus(st);
                } catch (TwitterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // Do something with value!
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Canceled.
            }
        });
        alert.show();
    }
}
