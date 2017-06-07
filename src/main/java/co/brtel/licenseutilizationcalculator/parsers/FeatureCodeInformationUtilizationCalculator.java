package co.brtel.licenseutilizationcalculator.parsers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import co.brtel.licenseutilizationcalculator.pojo.CapacityUnit;
import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;
import co.brtel.licenseutilizationcalculator.pojo.FeatureState;
import co.brtel.licenseutilizationcalculator.pojo.ManagedObject;
import co.brtel.licenseutilizationcalculator.pojo.Parameter;

public class FeatureCodeInformationUtilizationCalculator {
	private Map<String, List<ManagedObject>> managedObjectsMap;
	private static final String ENABLED = "Enabled";
	private static final String SUPPORTED = "Supported";
	private static final String WCEL = "WCEL";
	private static final String WBTS = "WBTS";
	public static final String NO_DOC_AVAILABLE = "No document available!";
	public static final String NOT_USED_IN_NETWORK = "Not used in network";
	public static final String NA = "N/A";
	public static final String COUNTER_NO_CALCULATION = "Counter, no calculation";

	public FeatureCodeInformationUtilizationCalculator(List<ManagedObject> managedObjects, Set<String> rncNames) {
		managedObjectsMap = new HashMap<String, List<ManagedObject>>();
		for (String rncName : rncNames) {
			List<ManagedObject> mos = managedObjects.stream().filter(item -> item.getDistName().contains("RNC-" + rncName.replace("R", "").replace("N", "")))
					.collect(Collectors.toList());
			managedObjectsMap.put(rncName, mos);
		}
	}

	public void calculateUtilization(List<FeatureInformation> featureInformations) {
		for (FeatureInformation featureInformation : featureInformations.stream()
				.filter(item -> item.getFeatureState() == FeatureState.ON && item.getCapacityUnit() != CapacityUnit.DYNAMIC_CELL && item.getCapacityUnit() != CapacityUnit.ON_OFF)
				.collect(Collectors.toList())) {
			calculate(featureInformation);
		}
		for (FeatureInformation featureInformation : featureInformations.stream()
				.filter(item -> item.getFeatureState() == FeatureState.OFF || item.getCapacityUnit() == CapacityUnit.DYNAMIC_CELL || item.getCapacityUnit() == CapacityUnit.ON_OFF)
				.collect(Collectors.toList())) {
			featureInformation.setUtilization(featureInformation.getCapacityUnit().toString());
		}
	}

	private void calculate(FeatureInformation featureInformation) {
		switch (featureInformation.getManagedObjectType()) {
		case RNFC:
			calculateUtilizationBasedOnRnfc(featureInformation);
			break;
		case WCEL:
			calculateUtilizationBasedOnWcell(featureInformation);
			break;
		case NONE:
			calculateUtilizationForNoneObjectType(featureInformation);
			break;
		default:
			break;
		}
	}
	
	private void calculateUtilizationForNoneObjectType(FeatureInformation featureInformation) {
		final String rncName = featureInformation.getRnc().getName();
		List<ManagedObject> wcells = managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL))
				.collect(Collectors.toList());
		long count = 0;
		if (featureInformation.getCode().equals("625")) {
			count = 0;
		} else if (featureInformation.getCode().equals("969")) {
			count = 0;
		} else if (featureInformation.getCode().equals("974")) {
			count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("1029")) {
			count = getWbtsCount(rncName);
		} else if (featureInformation.getCode().equals("1030")) {
			count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("1057")) {
			count = 0;
		} else if (featureInformation.getCode().equals("1079")) {
			count = getWbtsCount(rncName);
		} else if (featureInformation.getCode().equals("1107")) {
			featureInformation.setUtilization(NO_DOC_AVAILABLE);
		} else if (featureInformation.getCode().equals("1110")) {
			featureInformation.setUtilization(NO_DOC_AVAILABLE);
		} else if (featureInformation.getCode().equals("1246")) {
			featureInformation.setUtilization(NOT_USED_IN_NETWORK);
		} else if (featureInformation.getCode().equals("1435")) {
			featureInformation.setUtilization(NA);
		} else if (featureInformation.getCode().equals("1490")) {
			featureInformation.setUtilization(NO_DOC_AVAILABLE);
		} else if (featureInformation.getCode().equals("1683")) {
			featureInformation.setUtilization(NO_DOC_AVAILABLE);
		} else if (featureInformation.getCode().equals("1897")) {
			featureInformation.setUtilization(NO_DOC_AVAILABLE);
		} else if (featureInformation.getCode().equals("1898")) {
			featureInformation.setUtilization(NOT_USED_IN_NETWORK);
		} else if (featureInformation.getCode().equals("1938")) {
			count = 0;
		} else if (featureInformation.getCode().equals("2117")) {
			featureInformation.setUtilization(COUNTER_NO_CALCULATION);
		}
		if(featureInformation.getUtilization() == "-1")
			featureInformation.setUtilization(new Long(count).toString());
	}

	private String getParameterValue(ManagedObject managedObject, String name) {
		Optional<Parameter> hSUPAEnabled = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase(name)).findAny();
		if (hSUPAEnabled.isPresent())
			return hSUPAEnabled.get().getValue();
		return "";
	}

	private void calculateUtilizationBasedOnWcell(FeatureInformation featureInformation) {
		String rncName = featureInformation.getRnc().getName();
		long count = 0;
		List<ManagedObject> wcells = managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL))
				.collect(Collectors.toList());
		if (featureInformation.getCode().equals("638") || featureInformation.getCode().equals("966")) {
			count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("961") || featureInformation.getCode().equals("962")) {
			Map<String, Boolean> tmpMap = new HashMap<>();
			for (ManagedObject wcell : wcells) {
				String hSUPAEnabled = getParameterValue(wcell, "HSUPAEnabled");
				if (hSUPAEnabled.equalsIgnoreCase(ENABLED))
					tmpMap.put(Arrays.stream(wcell.getDistName().split("/")).filter(item -> item.startsWith(WBTS)).findAny().get(), true);
			}
			count = tmpMap.size();
		} else if (featureInformation.getCode().equals("1086")) {
			for (ManagedObject wcell : wcells) {
				String hSUPAEnabled = getParameterValue(wcell, "HSUPA2MSTTIEnabled");
				String maxTotalUplinkSymbolRate = getParameterValue(wcell, "MaxTotalUplinkSymbolRate");
				if (hSUPAEnabled.equalsIgnoreCase(ENABLED) && maxTotalUplinkSymbolRate.equalsIgnoreCase("3"))
					count++;
			}
		} else if (featureInformation.getCode().equals("1087")) {
			Map<String, Boolean> tmpMap = new HashMap<>();
			for (ManagedObject wcell : wcells) {
				String hSDPA64UsersEnabled = getParameterValue(wcell, "HSDPA64UsersEnabled");
				if (hSDPA64UsersEnabled.equalsIgnoreCase(ENABLED))
					tmpMap.put(Arrays.stream(wcell.getDistName().split("/")).filter(item -> item.startsWith(WBTS)).findAny().get(), true);
			}
			count = tmpMap.size();
		} else if (featureInformation.getCode().equals("1089")) {
			Map<String, Boolean> tmpMap = new HashMap<>();
			for (ManagedObject wcell : wcells) {
				String hSPAQoSEnabled = getParameterValue(wcell, "HSPAQoSEnabled");
				if (hSPAQoSEnabled.equalsIgnoreCase(ENABLED))
					tmpMap.put(Arrays.stream(wcell.getDistName().split("/")).filter(item -> item.startsWith(WBTS)).findAny().get(), true);
			}
			count = tmpMap.size();
		} else if (featureInformation.getCode().equals("1091")) {
			Map<String, Boolean> tmpMap = new HashMap<>();
			for (ManagedObject wcell : wcells) {
				String hSPAQoSEnabled = getParameterValue(wcell, "HspaMultiNrtRabSupport");
				if (hSPAQoSEnabled.equalsIgnoreCase(SUPPORTED))
					tmpMap.put(Arrays.stream(wcell.getDistName().split("/")).filter(item -> item.startsWith(WBTS)).findAny().get(), true);
			}
			count = tmpMap.size();
		} else if (featureInformation.getCode().equals("1471")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "HSDPA64QAMallowed").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1475")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "HSUPA2MSTTIEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1476")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "FDPCHEnabled").equalsIgnoreCase(ENABLED) && getParameterValue(wcell, "CPCEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1485")) {
			for (ManagedObject wcell : wcells) {
				// TODO: 1 cell found!
				if (getParameterValue(wcell, "PCH24kbpsEnabled").equalsIgnoreCase(ENABLED) && new Integer(getParameterValue(wcell, "NbrOfSCCPCHs")) > 1) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1497")) {
			for (ManagedObject wcell : wcells) {
				// TODO: 1 cell found!
				if (getParameterValue(wcell, "HSUPA16QAMAllowed").equalsIgnoreCase(ENABLED) && getParameterValue(wcell, "MaxTotalUplinkSymbolRate").equalsIgnoreCase("3")) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1755")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "LTECellReselection").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1756")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "IncomingLTEISHO").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1795")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "HSFACHEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1798")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "MDTPeriodicMeasEnabled").equalsIgnoreCase("1")) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3414")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "LTEHandoverEnabled").toLowerCase().contains(ENABLED.toLowerCase())) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3420")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "SABEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3422")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "AppAwareRANEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3898")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "LayeringRRCRelEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3903")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "SmartLTELayeringEnabled").toLowerCase().contains(ENABLED.toLowerCase())) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4160")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "FastHSPAMobilityEnabled").toLowerCase().contains(ENABLED.toLowerCase())) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4492")) {
			for (ManagedObject wcell : wcells) {
				if (new Integer(getParameterValue(wcell, "MassEventHandler")) > 0) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4533")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "HSFACHDRXEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4538")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "CPCEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4545")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "VoiceCallPriority").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4839")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "SmartLTELayeringEnabled").toLowerCase().contains(ENABLED.toLowerCase()))
					count++;
			}
		}
		featureInformation.setUtilization(new Long(count).toString());
	}

	private void calculateUtilizationBasedOnRnfc(FeatureInformation featureInformation) {
		long count = 0;
		final String rncName = featureInformation.getRnc().getName();

		Optional<ManagedObject> opt = managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase("RNFC")).findAny();
		if (!opt.isPresent())
			return;
		ManagedObject managedObject = opt.get();

		if (featureInformation.getCode().equals("630")) {
			if (getParameterValue(managedObject, "AMRWithEDCH").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("885")) {
			if (getParameterValue(managedObject, "EmergencyCallRedirect").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("967")) {
			if (getParameterValue(managedObject, "HSDPAMobility").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("968")) {
			if (getParameterValue(managedObject, "AMRWithHSDSCH").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("975")) {
			if (getParameterValue(managedObject, "SubscriberTrace").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("1060")) {
			if (getParameterValue(managedObject, "IFHOOverIurEnabled").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("1061")) {
			if (getParameterValue(managedObject, "HSDPAMobility").equalsIgnoreCase(ENABLED) && getParameterValue(managedObject, "HSPAInterRNCMobility").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("1062")) {
			if (getParameterValue(managedObject, "WireLessPriorityService").equalsIgnoreCase(ENABLED)
					&& getParameterValue(managedObject, "WPSCallRestriction").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("1686")) {
			if (getParameterValue(managedObject, "HSDPAMobility").equalsIgnoreCase(ENABLED) && getParameterValue(managedObject, "FRLCEnabled").equalsIgnoreCase(ENABLED)) {
				Stream<ManagedObject> wcels = managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL));
				for (ManagedObject wcell : wcels.collect(Collectors.toList())) {
					if (getParameterValue(wcell, "DCellHSDPAEnabled").equalsIgnoreCase(ENABLED))
						count++;
				}
			}
		} else if (featureInformation.getCode().equals("1690")) {
			if (getParameterValue(managedObject, "RRCSetupCCHEnabledR99").equalsIgnoreCase(ENABLED)) {
				Stream<ManagedObject> wcels = managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL));
				for (ManagedObject wcell : wcels.collect(Collectors.toList())) {
					if (getParameterValue(wcell, "CCHSetupEnabled").equalsIgnoreCase(ENABLED))
						count++;
				}
			}
		} else if (featureInformation.getCode().equals("3384")) {
			if (getParameterValue(managedObject, "LFDProfEnabled").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("4290")) {
			if (getParameterValue(managedObject, "AutoACResEnabled").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("4293")) {
			if (getParameterValue(managedObject, "FastPCHSwitchEnabled").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("4348")) {
			if (getParameterValue(managedObject, "DCH00SuppOnIurEnabled").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		} else if (featureInformation.getCode().equals("4780")) {
			if (getParameterValue(managedObject, "RRCReDirEnabled").equalsIgnoreCase(ENABLED))
				count = getWCellCount(rncName);
		}

		featureInformation.setUtilization(new Long(count).toString());
	}

	private long getWbtsCount(String rncName) {
		return managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(WBTS)).count();
	}
	
	private long getWCellCount(String rncName) {
		return managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count();
	}
	
	private long getWCellCount(ManagedObject wbts, String rncName) {
		return managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(WBTS)).filter(item -> item.getDistName().contains(wbts.getDistName())).count();
	}
}
