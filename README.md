# Kapt Leak Project

This project is meant to demonstrate a file leak that occurs in the Gradle process when buildig using `kapt`.

In order to see the file leak occur, download the file leak detector jar from here (https://github.com/jenkinsci/lib-file-leak-detector?tab=readme-ov-file#download) and replace the path for the jar set in the [`gradle.properties`](./gradle.properties) with the path to your leak detector jar.

Once this is done, build the app and save the output from http://localhost:19999. From there, follow the steps to do post-processing of the open file descriptor output here: https://github.com/centic9/file-leak-postprocess

After post-process, there should be a large grouping of open file descriptors that share this stacktrace:
```
	at java.base/java.nio.file.Files.newDirectoryStream(Files.java:482)
	at java.base/java.nio.file.FileTreeWalker.visit(FileTreeWalker.java:301)
	at java.base/java.nio.file.FileTreeWalker.walk(FileTreeWalker.java:323)
	at java.base/java.nio.file.Files.walkFileTree(Files.java:2804)
	at jdk.compiler/com.sun.tools.javac.file.JavacFileManager$ArchiveContainer.<init>(JavacFileManager.java:578)
	at jdk.compiler/com.sun.tools.javac.file.JavacFileManager.getContainer(JavacFileManager.java:332)
	at jdk.compiler/com.sun.tools.javac.file.JavacFileManager.pathsAndContainers(JavacFileManager.java:1080)
	at jdk.compiler/com.sun.tools.javac.file.JavacFileManager.indexPathsAndContainersByRelativeDirectory(JavacFileManager.java:1035)
	at java.base/java.util.HashMap.computeIfAbsent(HashMap.java:1220)
	at jdk.compiler/com.sun.tools.javac.file.JavacFileManager.pathsAndContainers(JavacFileManager.java:1023)
	at jdk.compiler/com.sun.tools.javac.file.JavacFileManager.list(JavacFileManager.java:779)
	at org.jetbrains.kotlin.kapt3.base.javac.KaptJavaFileManager.list(KaptJavaFileManager.kt:38)
	at jdk.compiler/com.sun.tools.javac.code.ClassFinder.list(ClassFinder.java:737)
	at jdk.compiler/com.sun.tools.javac.code.ClassFinder.scanUserPaths(ClassFinder.java:674)
	at jdk.compiler/com.sun.tools.javac.code.ClassFinder.fillIn(ClassFinder.java:552)
	at jdk.compiler/com.sun.tools.javac.code.ClassFinder.complete(ClassFinder.java:299)
	at jdk.compiler/com.sun.tools.javac.code.Symbol.complete(Symbol.java:682)
	at jdk.compiler/com.sun.tools.javac.code.Symbol$PackageSymbol.members(Symbol.java:1176)
	at jdk.compiler/com.sun.tools.javac.code.Symtab.listPackageModules(Symtab.java:863)
	at jdk.compiler/com.sun.tools.javac.comp.Enter.visitTopLevel(Enter.java:346)
	at jdk.compiler/com.sun.tools.javac.tree.JCTree$JCCompilationUnit.accept(JCTree.java:544)
	at jdk.compiler/com.sun.tools.javac.comp.Enter.classEnter(Enter.java:286)
	at jdk.compiler/com.sun.tools.javac.comp.Enter.classEnter(Enter.java:301)
	at jdk.compiler/com.sun.tools.javac.comp.Enter.complete(Enter.java:603)
	at jdk.compiler/com.sun.tools.javac.comp.Enter.main(Enter.java:587)
	at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.enterTrees(JavaCompiler.java:1042)
	at org.jetbrains.kotlin.kapt3.base.AnnotationProcessingKt.doAnnotationProcessing(annotationProcessing.kt:85)
	at org.jetbrains.kotlin.kapt3.base.AnnotationProcessingKt.doAnnotationProcessing$default(annotationProcessing.kt:33)
	at org.jetbrains.kotlin.kapt3.base.Kapt.kapt(Kapt.kt:47)
	...
	at org.jetbrains.kotlin.gradle.internal.KaptExecution.run(KaptWithoutKotlincTask.kt:313)
	at org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask$KaptExecutionWorkAction.execute(KaptWithoutKotlincTask.kt:259)
	at org.gradle.workers.internal.DefaultWorkerServer.execute(DefaultWorkerServer.java:63)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1$1.create(NoIsolationWorkerFactory.java:66)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1$1.create(NoIsolationWorkerFactory.java:62)
	...
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1.lambda$execute$0(NoIsolationWorkerFactory.java:62)
	at org.gradle.workers.internal.AbstractWorker$1.call(AbstractWorker.java:44)
	at org.gradle.workers.internal.AbstractWorker$1.call(AbstractWorker.java:41)
	...
	at org.gradle.workers.internal.AbstractWorker.executeWrappedInBuildOperation(AbstractWorker.java:41)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1.execute(NoIsolationWorkerFactory.java:59)
	at org.gradle.workers.internal.DefaultWorkerExecutor.lambda$submitWork$0(DefaultWorkerExecutor.java:170)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	...
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	...
```

Consecutive builds with the same Gradle daemon causes kapt to open more files.
