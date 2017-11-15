package gov.lanl.micot.infrastructure.ep.io.pfw;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorImpl;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Class for turning a multi generator data into a single one, when necessary
 * @author Russell Bent
 *
 */
public class MultiGenerator extends GeneratorImpl {

	private Collection<Generator> data = null;
	
	/**
	 * Constructor
	 * @param data
	 */
	public MultiGenerator(Collection<Generator> data) {
	  super(data.iterator().next().getAttribute(Generator.ASSET_ID_KEY, Long.class));
		this.data = data;
	}

	@Override
	public Number getReactiveGeneration() {
		Number sum = 0;
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    sum = MathUtils.ADD(d.getReactiveGeneration(),sum);
		  }
		}
		return sum;
	}

	@Override
	public Number getRealGeneration() {
		Number sum = 0;
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    sum = MathUtils.ADD(d.getRealGeneration(),sum);
		  }
		}
		return sum;
	}

	@Override
	public double getDesiredReactiveMax() {
		double sum = 0;
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    sum += d.getDesiredReactiveMax();
		  }
		}
		return sum;

	}

	@Override
	public double getReactiveMin() {
		double sum = 0;
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    sum += d.getReactiveMin();
		  }
		}
		return sum;

	}

	@Override
	public double getDesiredRealGenerationMax() {
		double sum = 0;
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    sum += d.getDesiredRealGenerationMax();
		  }
		}
		return sum;

	}

	@Override
	public double getRealGenerationMin() {
		double sum = 0;
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    sum += d.getRealGenerationMin();
		  }
		}
		return sum;

	}

	@Override
	public boolean getStatus() {
	  boolean status = false;
	  for (Generator d : data) {
	    status |= d.getStatus();
	  }
	  return status;
	}

	@Override
	public void setReactiveGeneration(Number reactive) {
		Number sum = getReactiveGeneration();
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    d.setReactiveGeneration(MathUtils.MULTIPLY(MathUtils.DIVIDE(d.getReactiveGeneration(),sum),reactive));
		  }
		}
	}

	@Override
	public void setRealGeneration(Number real) {
		Number sum = getRealGeneration();
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    d.setRealGeneration(MathUtils.MULTIPLY(MathUtils.DIVIDE(d.getRealGeneration(),sum),real));
		  }
		}
	}

	@Override
	public void setDesiredReactiveMax(double max) {
		double sum = getDesiredReactiveMax();
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    d.setDesiredReactiveMax((d.getDesiredReactiveMax() / sum) * max);
		  }
		}
	}

	@Override
	public void setReactiveMin(double max) {
		double sum = getReactiveMin();
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    d.setReactiveMin((d.getReactiveMin() / sum) * max);
		  }
		}
	}

	@Override
	public void setDesiredRealGenerationMax(double real) {
		double sum = getDesiredRealGenerationMax();
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    d.setDesiredRealGenerationMax((d.getDesiredRealGenerationMax() / sum) * real);
		  }
		}
	}

	@Override
	public void setRealGenerationMin(double real) {
		double sum = getRealGenerationMin();
		for (Generator d : data) {
		  if (d.getStatus() == true) {
		    d.setRealGenerationMin((d.getRealGenerationMin() / sum) * real);
		  }
		}
	}

	@Override
	public void setStatus(boolean b) {
		for (Generator d : data) {
			d.setStatus(b);
		}
	}

  @Override
  public GeneratorTypeEnum getType() {
    return data.iterator().next().getType();
  }

  @Override
  public void setType(GeneratorTypeEnum type) {
    for (Generator d : data) {
      d.setType(type);
    }
  }

  @Override
  public Point getCoordinate() {
    return data.iterator().next().getCoordinate();
  }

  @Override
  public void setCoordinate(Point point) {
    for (Generator d : data) {
      d.setCoordinate(point);
    }
  }

  @Override
  public int compareTo(Asset arg0) {
    return data.iterator().next().compareTo(arg0);
  }

  @Override
  public Object getAttribute(Object key) {
    return data.iterator().next().getAttribute(key);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <E> E getAttribute(Object key, Class<E> cls) {
    return (E)getAttribute(key);
  }
  
  @Override
  public void setAttribute(Object key, Object value) {
    if (key.equals(Generator.ASSET_ID_KEY)) {
      return;
    }    
    for (Generator generator : data) {
      generator.setAttribute(key,value);
    }
  }
  
}
