package org.kie.uberfire.social.activities.client.gravatar;

import org.gwtbootstrap3.client.ui.Image;

public class GravatarImage extends Image {

    public GravatarImage( final String email,
                          final int size ) {
        setUrl( GravatarUrlBuilder.get().build( email, size ) );
    }

}
