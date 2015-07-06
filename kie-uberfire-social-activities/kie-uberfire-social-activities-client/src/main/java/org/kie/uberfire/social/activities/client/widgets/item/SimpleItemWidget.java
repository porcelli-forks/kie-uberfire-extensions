package org.kie.uberfire.social.activities.client.widgets.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.ThumbnailPanel;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.kie.uberfire.social.activities.client.widgets.item.model.SimpleItemWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.utils.SocialDateFormatter;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.client.resources.UberfireResources;
import org.uberfire.client.workbench.type.ClientResourceType;

public class SimpleItemWidget extends Composite {

    @UiField
    Column icon;

    @UiField
    Column link;

    @UiField
    Column desc;

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    private static final com.google.gwt.user.client.ui.Image GENERIC_FILE_IMAGE = new com.google.gwt.user.client.ui.Image( UberfireResources.INSTANCE.images().typeGenericFile() );


    interface MyUiBinder extends UiBinder<Widget, SimpleItemWidget> {

    }

    public void init( SimpleItemWidgetModel model ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        if ( !model.shouldIPrintIcon() ) {
            createThumbNail( model.getSocialUser() );
        } else {
            createIcon( model );
        }
        createColumnContent( model );
    }

    private void createColumnContent( SimpleItemWidgetModel model ) {
        if ( model.getLinkText() != null ) {
            link.add( createLink( model ) );
        } else {
            link.add( new Paragraph( model.getDescription() ) );
        }
        desc.add( createText( model ) );
    }

    private Paragraph createText( SimpleItemWidgetModel model ) {
        StringBuilder sb = new StringBuilder();
        sb.append( model.getItemDescription() );
        sb.append( SocialDateFormatter.format( model.getTimestamp() ) );
        sb.append( " by " + model.getSocialUser().getUserName() );
        return new Paragraph( sb.toString() );
    }

    private void createIcon( final SimpleItemWidgetModel model ) {
        if ( model.isVFSLink() ) {
            for ( ClientResourceType type : model.getResourceTypes() ) {
                if ( type.accept( model.getLinkPath() ) ) {
                    com.google.gwt.user.client.ui.Image maybeAlreadyAttachedImage = (com.google.gwt.user.client.ui.Image) type.getIcon();
                    Image newImage = new Image( maybeAlreadyAttachedImage.getUrl(), maybeAlreadyAttachedImage.getOriginLeft(), maybeAlreadyAttachedImage.getOriginTop(), maybeAlreadyAttachedImage.getWidth(), maybeAlreadyAttachedImage.getHeight() );
                    icon.add( newImage );
                    break;
                }
            }
        } else {
            com.google.gwt.user.client.ui.Image maybeAlreadyAttachedImage = GENERIC_FILE_IMAGE;
            Image newImage = new Image( maybeAlreadyAttachedImage.getUrl(), maybeAlreadyAttachedImage.getOriginLeft(), maybeAlreadyAttachedImage.getOriginTop(), maybeAlreadyAttachedImage.getWidth(), maybeAlreadyAttachedImage.getHeight() );
            icon.add( newImage );
        }
    }

    private ListGroup createLink( final SimpleItemWidgetModel model ) {
        ListGroup list = new ListGroup();
        ListGroupItem link = new ListGroupItem();
        link.setText( model.getLinkText() );
        link.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                model.getLinkCommand().execute( new LinkCommandParams( model.getEventType(),
                        model.getLinkURI(),
                        model.getLinkType() )
                        .withLinkParams( model.getLinkParams() ) );
            }
        }, ClickEvent.getType() );
        list.add( link );
        return list;
    }

    private void createThumbNail( SocialUser socialUser ) {
        ThumbnailPanel tumThumbnails = new ThumbnailPanel();
        Image userImage;
        userImage = GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.SMALL );
        userImage.setSize( "30px", "30px" );
        tumThumbnails.add( userImage );
        icon.add( tumThumbnails );
    }

}
