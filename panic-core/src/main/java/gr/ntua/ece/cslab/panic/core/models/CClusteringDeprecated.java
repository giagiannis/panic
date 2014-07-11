package gr.ntua.ece.cslab.panic.core.models;

import weka.classifiers.meta.ClassificationViaClustering;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class CClusteringDeprecated extends AbstractWekaModel {

    public CClusteringDeprecated() {
        super();
        this.classifier = new ClassificationViaClustering();
    }

    @Override
    public void configureClassifier() {
        // nothing here
    }
    
}
