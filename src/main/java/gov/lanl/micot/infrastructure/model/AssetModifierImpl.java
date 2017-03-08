package gov.lanl.micot.infrastructure.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for performing an asset modification that sets a particular value to the attribute
 * 
 * @author Russell Bent
 */
public class AssetModifierImpl implements AssetModifier {

	private String attribute = null;
	private Object newValue = null;
	private Map<Asset, Object> oldValue = null;

	/**
	 * Constructor
	 * 
	 * @param attribute
	 * @param newVal
	 */
	public AssetModifierImpl(String attribute, Object newVal) {
		super();
		this.attribute = attribute;
		this.newValue = newVal;
		oldValue = new HashMap<Asset, Object>();
	}

	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * @return the newValue
	 */
	public Object getNewValue() {
		return newValue;
	}

	@Override
	public void modifyAsset(Asset asset) {
		oldValue.put(asset, asset.getAttribute(attribute));
		asset.setAttribute(attribute, newValue);
	}

	@Override
	public void unModifyAsset(Asset asset) {
		asset.setAttribute(attribute, oldValue.get(asset));
		oldValue.remove(asset);
	}

	@Override
	public String toString() {
		return attribute + "=" + newValue;
	}

}
