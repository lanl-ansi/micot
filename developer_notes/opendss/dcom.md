# Open DSS DCOM

The open dss dcom API is poorly documented and it is difficult to figure out how to interact with OpenDSS in this way.  The "best" way to do so is by inspecting the DCOM
library directly.  To do this, follow these steps

1. Open Microsoft Excel
2. Goto to the Developer's tab
	a. If you don't have the developer's tab visible, right click in the ribbon area and under main, make sure developer is checked.
3. Click on the Visual Basic icon on the left hand side of the ribbon.
4. Goto Tools -> References and add "OpenDSS Engine" from the DCOM list
4. Gotto View -> Object Browser and select "OpenDSS Engine".  This should show and index of all the function calls and arguments they need. Generally, the names are pretty self
explanatory.

Note that the opendss DCOM API does not support getting information about the vsource object.  By naming convention, we look for a bus called "sourcebus" and use that
as the source (connection to the transmission system) of most power.  In the absence of this, you will need to add a dummy generator with infinite capacity where
the vsource is so that it can be modeled appropriately.