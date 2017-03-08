package gov.lanl.micot.infrastructure.model;


/**
 * Interface for creating models from other models
 * @author Russell Bent
 */
public interface ModelFactory<M extends Model> {

	/**
	 * Constructor a model from another model
	 * @param model
	 * @return
	 */
	public Model constructModel(Model model);
	
}
