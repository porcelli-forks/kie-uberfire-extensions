package org.kie.uberfire.social.activities.client.widgets.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.kie.uberfire.social.activities.client.widgets.item.model.SocialItemExpandedWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.resources.UberfireResources;
import org.uberfire.client.workbench.type.ClientResourceType;

public class SocialItemExpandedWidget extends Composite {

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    private static final com.google.gwt.user.client.ui.Image GENERIC_FILE_IMAGE = new com.google.gwt.user.client.ui.Image( UberfireResources.INSTANCE.images().typeGenericFile() );

    @UiField
    Column icon;

    @UiField
    Column file;

    @UiField
    Container table;

    interface MyUiBinder extends UiBinder<Widget, SocialItemExpandedWidget> {

    }

    public void init( SocialItemExpandedWidgetModel model ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        createItem( model );
    }

    public void createItem( SocialItemExpandedWidgetModel model ) {
        createFirstRow( model );
        for ( UpdateItem updateItem : model.getUpdateItems() ) {
            createSecondRow( model, updateItem );
        }

    }

    public void createFirstRow(
            SocialItemExpandedWidgetModel model ) {
        createIcon( model );
        createLink( model );

    }

    private void createIcon( final SocialItemExpandedWidgetModel model ) {

        UpdateItem updateItem = model.getUpdateItems().get( 0 );
        if ( updateItem.getEvent().isVFSLink() ) {
            MessageBuilder.createCall( new RemoteCallback<Path>() {
                public void callback( Path path ) {
                    for ( ClientResourceType type : model.getModel().getResourceTypes() ) {
                        if ( type.accept( path ) ) {
                            com.google.gwt.user.client.ui.Image maybeAlreadyAttachedImage = (com.google.gwt.user.client.ui.Image) type.getIcon();
                            Image newImage = new Image( maybeAlreadyAttachedImage.getUrl(), maybeAlreadyAttachedImage.getOriginLeft(), maybeAlreadyAttachedImage.getOriginTop(), maybeAlreadyAttachedImage.getWidth(), maybeAlreadyAttachedImage.getHeight() );
                            icon.add( newImage );
                            break;
                        }
                    }
                }
            }, VFSService.class ).get( updateItem.getEvent().getLinkTarget() );

        } else {
            //TODO, provide icons per event type.
            com.google.gwt.user.client.ui.Image maybeAlreadyAttachedImage = GENERIC_FILE_IMAGE;
            Image newImage = new Image( maybeAlreadyAttachedImage.getUrl(), maybeAlreadyAttachedImage.getOriginLeft(), maybeAlreadyAttachedImage.getOriginTop(), maybeAlreadyAttachedImage.getWidth(), maybeAlreadyAttachedImage.getHeight() );
            icon.add( newImage );
        }
    }

    private void createLink( final SocialItemExpandedWidgetModel model ) {
        final UpdateItem updateItem = model.getUpdateItems().get( 0 );
        ListGroup list = new ListGroup();
        ListGroupItem link = new ListGroupItem();
        final String linkLabel = updateItem.getEvent().getLinkLabel();
        link.setText( linkLabel );
        link.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                model.getModel().getLinkCommand().execute( new LinkCommandParams( updateItem.getEvent().getType(),
                        updateItem.getEvent().getLinkTarget(),
                        updateItem.getEvent().getLinkType() )
                        .withLinkParams( updateItem.getEvent().getLinkParams() ) );
            }
        }, ClickEvent.getType() );
        list.add( link );
        file.add( list );
    }

    public void createSecondRow( final SocialItemExpandedWidgetModel model,
                                 final UpdateItem updateItem ) {

        MessageBuilder.createCall( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser socialUser ) {
                CommentRowWidget row = GWT.create( CommentRowWidget.class );
                updateItem.setSocialUser( socialUser );
                updateItem.setUserClickCommand( model.getModel().getUserClickCommand() );
                updateItem.setFollowUnfollowCommand( model.getModel().getFollowUnfollowCommand() );
                updateItem.setLoggedUser( model.getModel().getSocialUser() );
                row.init( updateItem );
                table.add( row );
            }
        }, SocialUserRepositoryAPI.class ).findSocialUser( updateItem.getEvent().getSocialUser().getUserName() );

    }

}
