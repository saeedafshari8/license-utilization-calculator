package co.brtel.licenseutilizationcalculator.parsers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.NumberUtils;

import co.brtel.licenseutilizationcalculator.pojo.CapacityUnit;
import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;
import co.brtel.licenseutilizationcalculator.pojo.FeatureState;
import co.brtel.licenseutilizationcalculator.pojo.ManagedObject;
import co.brtel.licenseutilizationcalculator.pojo.ManagedObjectType;
import co.brtel.licenseutilizationcalculator.pojo.Parameter;

public class FeatureCodeInformationUtilizationCalculator {
	private static final String BTS_SUPPORT_FOR_HSPACM_DISABLED = "Only DCH CM for IFHO, LTE and GSM";
	private Map<String, List<ManagedObject>> managedObjectsMap;
	private String LTE_LAYERING_MEAS_ACTIVATION = "Measurement for all triggers";
	private String PS_RAB_SUPPORTED = "PS RAB Reconfiguration is supported";
	private String TQM_NOT_CONNECTED = "TQM object is not connected to the WBTS";
	private String OVERBOOKING_ON = "Overbooking ON";
	private static final String ENABLED = "Enabled";
	private static final String SUPPORTED = "Supported";
	private static final String WCEL = "WCEL";
	private static final String USED = "USED";
	private static final String WBTS = "WBTS";
	public static final String NO_DOC_AVAILABLE = "No document available!";
	public static final String NOT_USED_IN_NETWORK = "Not used in network";
	public static final String NA = "N/A";
	public static final String COUNTER_NO_CALCULATION = "Counter, no calculation";
	private static final String FMCG = "FMCG";
	private static final String RNMOBI = "RNMOBI";

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
		case WBTS:
			calculateUtilizationBasedOnWbts(featureInformation);
			break;
		case RNHSPA:
			calculateUtilizationBasedOnRNHSPA(featureInformation);
			break;
		case FMCG:
			calculateUtilizationBasedOnFMCG(featureInformation);
			break;
		case RNMOBI:
			calculateUtilizationBasedOnRNMOBI(featureInformation);
			break;
		default:
			break;
		}
	}

	private void calculateUtilizationBasedOnRNMOBI(FeatureInformation featureInformation) {
		long count = 0;
		String rncName = featureInformation.getRnc().getName();
		Optional<ManagedObject> rnmobi = managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(RNMOBI)).findAny();

		if (!rnmobi.isPresent())
			return;

		if (featureInformation.getCode().equals("628")) {
			if (getParameterValue(rnmobi.get(), "DirectedRRCForHSDPALayerEnhanc").equalsIgnoreCase(ENABLED)) {
				for (ManagedObject wcell : managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).collect(Collectors.toList())) {
					if (getParameterValue(wcell, "HSDPALayeringCommonChEnabled").equalsIgnoreCase(ENABLED)) {
						count++;
					}
				}
			}
		}
		if (featureInformation.getUtilization().equals(FeatureInformation.DEFAULT_UTILIZATION))
			featureInformation.setUtilization(new Long(count).toString());
	}

	private void calculateUtilizationBasedOnFMCG(FeatureInformation featureInformation) {
		long count = 0;
		String rncName = featureInformation.getRnc().getName();
		List<ManagedObject> fmcgs = managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(FMCG))
				.collect(Collectors.toList());

		if (featureInformation.getCode().equals("1302")) {
			for (ManagedObject fmcg : fmcgs) {
				if (getParameterValue(fmcg, "AMRDirReCell").equalsIgnoreCase(ENABLED)) {
					Optional<String> fmcgId = Arrays.stream(fmcg.getDistName().split("/")).filter(item -> item.startsWith(FMCG)).map(item -> item.replace(FMCG + "-", ""))
							.findAny();
					if (fmcgId.isPresent()) {
						for (ManagedObject wcell : managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL))
								.collect(Collectors.toList())) {
							if (getParameterValue(wcell, "RtFmcgIdentifier").equalsIgnoreCase(fmcgId.get()))
								count++;
						}
					}
				}
			}
		}
		if (featureInformation.getUtilization().equals(FeatureInformation.DEFAULT_UTILIZATION))
			featureInformation.setUtilization(new Long(count).toString());
	}

	private void calculateUtilizationBasedOnRNHSPA(FeatureInformation featureInformation) {
		long count = 0;
		String rncName = featureInformation.getRnc().getName();
		
		Optional<ManagedObject> opt = managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(ManagedObjectType.RNHSPA.toString())).findAny();
		if (!opt.isPresent())
			return;
		ManagedObject managedObject = opt.get();
		
		if (featureInformation.getCode().equals("1028")) {
			String max = getParameterValue(managedObject, "MaxBitRateNRTMACDFlow");
			if(NumberUtils.isCreatable(max)){
				Integer tmp = new Integer(max);
				if(tmp == 0 || tmp > 9600)
					count = getWCellCount(rncName);
			}
		}
		if(featureInformation.getUtilization().equals(FeatureInformation.DEFAULT_UTILIZATION))
			featureInformation.setUtilization(new Long(count).toString());
	}

	private void calculateUtilizationBasedOnWbts(FeatureInformation featureInformation) {
		String rncName = featureInformation.getRnc().getName();
		long count = 0;
		List<ManagedObject> wbtses = managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase(WBTS))
				.collect(Collectors.toList());

		if (featureInformation.getCode().equals("633")) {
			// TODO : Value is not mentioned
			for (ManagedObject wbts : wbtses) {
				if (getParameterValue(wbts, "OverbookingSwitch").equalsIgnoreCase(OVERBOOKING_ON ))
					count++;
			}
		} else if (featureInformation.getCode().equals("641")) {
			for (ManagedObject wbts : wbtses) {
				count++;
			}
		} else if (featureInformation.getCode().equals("1085")) {
			for (ManagedObject wbts : wbtses) {
				if (getParameterValue(wbts, "HSDPA14MbpsPerUser").equalsIgnoreCase(ENABLED))
					count++;
			}
		} else if (featureInformation.getCode().equals("1088")) {
			for (ManagedObject wbts : wbtses) {
				if (getParameterValue(wbts, "HSUPAXUsersEnabled").contains("60"))
					count++;
			}
		} else if (featureInformation.getCode().equals("1092")) {
			for (ManagedObject wbts : wbtses) {
				if (getParameterValue(wbts, "NodeBRABReconfigSupport").equalsIgnoreCase(PS_RAB_SUPPORTED)){
					count = getWCellCount(wbts, rncName);
				}
			}
		} else if (featureInformation.getCode().equals("1093")) {
			for (ManagedObject wbts : wbtses) {
				if (getParameterValue(wbts, "HSDPACCEnabled").equalsIgnoreCase(ENABLED))
					count++;
			}
		} else if (featureInformation.getCode().equals("1247")) {
			for (ManagedObject wbts : wbtses) {
				if (!getParameterValue(wbts, "TQMId").equalsIgnoreCase(TQM_NOT_CONNECTED ))
					count++;
			}
		} else if (featureInformation.getCode().equals("1279")) {
			Optional<ManagedObject> opt = managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase("RNFC")).findAny();
			if (!opt.isPresent())
				return;
			ManagedObject rnfc = opt.get();
			if (getParameterValue(rnfc, "CMmasterSwitch").equalsIgnoreCase(USED)) {
				for (ManagedObject wbts : wbtses) {
					if (!getParameterValue(wbts, "BTSSupportForHSPACM").equalsIgnoreCase(BTS_SUPPORT_FOR_HSPACM_DISABLED))
						count += getWCellCount(wbts, rncName);
				}
			}
		} else if (featureInformation.getCode().equals("1303")) {
			for (ManagedObject wbts : wbtses) {
				if (getParameterValue(wbts, "SatelliteIubUsage").equalsIgnoreCase(ENABLED))
					count++;
			}
		} else if (featureInformation.getCode().equals("4783")) {
			for (ManagedObject wbts : wbtses) {
				if (!getParameterValue(wbts, "BTSSupportForHSPACM").equalsIgnoreCase(BTS_SUPPORT_FOR_HSPACM_DISABLED))
					count++;
			}
		}

		featureInformation.setUtilization(new Long(count).toString());
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
		Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase(name)).findAny();
		if (parameter.isPresent())
			return parameter.get().getValue();
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
				if (getParameterValue(wcell, "VoiceCallPriority").equalsIgnoreCase(ENABLED) && getParameterValue(wcell, "HSRACHEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4839")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "SmartLTELayeringEnabled").toLowerCase().contains(ENABLED.toLowerCase()) 
						&& getParameterValue(wcell, "LTELayeringMeasActivation").equals(LTE_LAYERING_MEAS_ACTIVATION ))
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
		return managedObjectsMap.get(rncName).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).filter(item -> item.getDistName().contains(wbts.getDistName())).count();
	}
}
