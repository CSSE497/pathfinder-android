package xyz.thepathfinder.android;

import java.util.Collection;
import java.util.List;

/**
 * The <tt>ClusterListener</tt> interface gives developers access to notifications
 * when a cluster is modified. These notifications include changes to commodity,
 * sub-clusters, and transports. They also include route notifications and others.
 *
 * @author David Robinson
 */
public abstract class ClusterListener extends Listener<Cluster> {

    /**
     * Invoked when the cluster receives a routed message.
     *
     * @param routes the cluster's routes as a list.
     */
    public void routed(List<Route> routes) {}

    /**
     * Invoked when a commodity was added to the cluster by a message.
     *
     * @param commodity added to the cluster.
     */
    public void commodityAdded(Commodity commodity) {}

    /**
     * Invoked when a commodity was removed from the cluster by a message.
     *
     * @param commodity removed from the cluster.
     */
    public void commodityRemoved(Commodity commodity) {}

    /**
     * Invoked when a commodity in this cluster was updated by a message.
     *
     * @param commodity the commodity updated.
     */
    public void commodityUpdated(Commodity commodity) {}

    /**
     * Invoked when at least one commodity was updated by a message.
     *
     * @param commodities that belong to this cluster.
     */
    public void commoditiesUpdated(Collection<Commodity> commodities) {}

    /**
     * Invoked when a subcluster was added to this cluster by a message.
     *
     * @param cluster the subcluster added to this cluster.
     */
    public void subclusterAdded(Cluster cluster) {}

    /**
     * Invoked when a subcluster was removed from this cluster by a message.
     *
     * @param cluster the subcluster removed from this cluster.
     */
    public void subclusterRemoved(Cluster cluster) {}

    /**
     * Invoked when a subcluster was updated from this cluster by a message.
     *
     * Note, this update doesn't recurse up the cluster tree. If the cluster
     * <tt>"/default/c1/c2/c3"</tt> receives an update message, both cluster
     * <tt>"/defautl/c1/c2/c3"</tt> and <tt>"/default/c1/c2"</tt> will receive
     * this message. This is because <tt>"/default/c1/c2/c3</tt> was modified
     * by the received message, but <tt>"/default/c1/c2"</tt> was not modified.
     *
     * @param cluster the cluster updated.
     */
    public void subclusterUpdated(Cluster cluster) {}

    /**
     * Invoked when a sublcuster was updated from this cluster by a message.
     *
     * Note, this update doesn't recurse up the cluster tree. If the cluster
     * <tt>"/default/c1/c2/c3"</tt> receives an update message, both cluster
     * <tt>"/defautl/c1/c2/c3"</tt> and <tt>"/default/c1/c2"</tt> will receive
     * this message. This is because <tt>"/default/c1/c2/c3</tt> was modified
     * by the received message, but <tt>"/default/c1/c2"</tt> was not modified.
     *
     * @param clusters the subclusters that belong to this cluster.
     */
    public void subclustersUpdated(Collection<Cluster> clusters) {}

    /**
     * Invoked when a transport was added to this cluster by a message.
     *
     * @param transport added by the message.
     */
    public void transportAdded(Transport transport) {}

    /**
     * Invoked when a transport was removed from this cluster by a message.
     *
     * @param transport removed by the message.
     */
    public void transportRemoved(Transport transport) {}

    /**
     * Invoked when a transport was updated in this cluster by a message.
     *
     * @param transport updated by the message.
     */
    public void transportUpdated(Transport transport) {}

    /**
     * Invoked when a transport was updated in this cluster by a message.
     *
     * @param transports that belong to this cluster.
     */
    public void transportsUpdated(Collection<Transport> transports) {}
}
