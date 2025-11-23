package net.nenko.utils.skulker;

/**
 * Executes commands
 *
 * Called from App with Context
 * If needed, browses files hierarchy and make call(s) to Strategy
 */
public class Executor {
//    private static final String ABORT_MSG = "Abort without any changes to files.";
//    /**
//     * Executes ecrypt operation over one or several files, depending on skulkedPath is file or directory
//     * @param cntx Context with all needed data
//     */
//    public void doEncrypt(Context cntx) {
//        Strategy strategy = loadStrategy(cntx.strategy);
//        if (isSingleFile(cntx.skulkedPath)) {
//            String carrierFile = getCarrierFile(cntx.carrierPath);
//            if (carrierFile == null) {
//                App.log.error("No carrier file found in {}. {}", cntx.carrierPath, ABORT_MSG);
//            }
//            strategy.encrypt(cntx.skulkedPath, carrierFile);
//        } else {
//            List<String> skulkedFiles = getSculkedFiles(cntx.skulkedPath, cntx.skulkedExt);
//            int tasksCount = skulkedFiles.size();
//            if (tasksCount < 1) {
//                App.log.error("No input files found to skulk. {}", ABORT_MSG);
//            }
//            List<String> carrierFiles = getCarrierFiles(cntx.carrierPath, cntx.carrierExt, tasksCount);
//            if (carrierFiles.size() < tasksCount) {
//                App.log.error("Having {} input files to skulk, only {} carrier files found. {}",
//                        tasksCount, carrierFiles.size(), ABORT_MSG);
//                return;
//            }
//            for (int i = 0; i < tasksCount; i++) {
//                strategy.encrypt(skulkedFiles.get(i), carrierFiles.get(i));
//            }
//        }
//    }
//
//    private static boolean isSingleFile(String path) {
//        File file = new File(path);
//        return file.isFile();      // Check if it's a regular file
//    }
//
//    private static String getCarrierFile(String path) {
//        if(isSingleFile(path)) {
//            return path;
//        }
//        List<String> carrierFiles = getCarrierFiles(path, 1);
//        return carrierFiles.size() == 1 ? carrierFiles.get(0) : null;
//    }
//
//    private static Strategy loadStrategy(String strategyName) {
//        return new StrategyDefault();
//    }
//
}
