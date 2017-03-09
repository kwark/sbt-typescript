package be.venneborg.typescript

import be.venneborg.ResourceHelper
import com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys
import com.typesafe.sbt.jse.SbtJsTask
import sbt.Keys._
import sbt._
import spray.json.{JsBoolean, JsNumber, JsObject, JsString}

object Import {

  object TypeScriptKeys {

    val typescript = TaskKey[Seq[File]]("tsc", "Invoke the TypeScript compiler.")

    val modules = SettingKey[Seq[String]]("typescript-modules", "A list of file names to be exported as modules (using TypeScript --out parameter).")

    val esTarget = SettingKey[EsTarget.Value]("typescript-opt-target", "The ECMAScript version TypeScript should target: by default ES5.")

    object EsTarget extends Enumeration {
      val ES3, ES5, ES6 = Value
    }

    val sourceMap = SettingKey[Boolean]("typescript-opt-sourceMap", "Whether TypeScript should generate corresponding .map files.")

    object ModuleType extends Enumeration {
      val None, Amd, CommonJs, ExtJs, ES6, Umd, System = Value
    }

    val moduleType = SettingKey[ModuleType.Value]("typescript-opt-module", "Specify module code generation: 'None', 'Commonjs' or 'Amd', 'Umd', 'System', 'ES6' or 'ExtJs' (default CommonJs)")

    val noImplicitAny = SettingKey[Boolean]("typescript-opt-noImplicitAny", "Warn on expressions and declarations with an implied 'any' type.")

    val removeComments = SettingKey[Boolean]("typescript-opt-removeComments", "Do not emit comments to output.")

  }

}

object SbtTypeScript extends AutoPlugin {

  override def requires = SbtJsTask
  override def trigger = AllRequirements
  val autoImport = Import

  import com.typesafe.sbt.jse.SbtJsTask.autoImport.JsTaskKeys._
  import be.venneborg.typescript.SbtTypeScript.autoImport.TypeScriptKeys._
  import com.typesafe.sbt.web.SbtWeb.autoImport._

  val defaults = Seq(
    modules := Seq.empty,
    esTarget := EsTarget.ES5,
    sourceMap := false,
    moduleType := ModuleType.CommonJs,
    noImplicitAny := false,
    removeComments := false,
    JsEngineKeys.parallelism := 1 //Typescript compiler only works when running only a single instance
  )

  val typeScriptSettings = Seq(

    includeFilter in typescript := GlobFilter("*.ts"),
    excludeFilter in typescript := GlobFilter("*.d.ts"),

    jsOptions := JsObject(
      "outDir" -> JsString((resourceManaged in typescript in Assets).value.getAbsolutePath),
      "target" -> JsNumber((esTarget in Assets).value match {
        case EsTarget.ES3 => 0
        case EsTarget.ES5 => 1
        case EsTarget.ES6 => 2
      }),
      "sourceMap" -> JsBoolean((sourceMap in Assets).value),
      "noImplicitAny" -> JsBoolean((noImplicitAny in Assets).value),
      "removeComments" -> JsBoolean((removeComments in Assets).value),
      "module" -> JsNumber((moduleType in Assets).value match {
        case ModuleType.None => 0
        case ModuleType.CommonJs => 1
        case ModuleType.Amd => 2
        case ModuleType.Umd => 3
        case ModuleType.System => 4
        case ModuleType.ES6 => 5
        case ModuleType.ExtJs => 99
      })
    ).toString()
  )

  override def projectSettings = defaults ++ inTask(typescript)(
    SbtJsTask.jsTaskSpecificUnscopedSettings ++ Seq(
      shellSource := {
        ResourceHelper.copyResourceTo(
          (target in Plugin).value / moduleName.value,
          getClass.getClassLoader.getResource("typescript/lib.d.ts"),
          streams.value.cacheDirectory / "copy-resource"
        )

        ResourceHelper.copyResourceTo(
          (target in Plugin).value / moduleName.value,
          getClass.getClassLoader.getResource("typescript/lib.es6.d.ts"),
          streams.value.cacheDirectory / "copy-resource"
        )

        ResourceHelper.copyResourceTo(
          (target in Plugin).value / moduleName.value,
          getClass.getClassLoader.getResource("typescript/tsc.js"),
          streams.value.cacheDirectory / "copy-resource"
        )

        ResourceHelper.copyResourceTo(
          (target in Plugin).value / moduleName.value,
          shellFile.value,
          streams.value.cacheDirectory / "copy-resource"
        )
      }) ++
    inConfig(Assets)(typeScriptSettings) ++
    inConfig(TestAssets)(typeScriptSettings) ++
    Seq(
      moduleName := "tsc",
      shellFile := getClass.getClassLoader.getResource("typescript/sbt-typescript.js"),

      taskMessage in Assets := "TypeScript compiling",
      taskMessage in TestAssets := "TypeScript test compiling"
    )
  ) ++ SbtJsTask.addJsSourceFileTasks(typescript) ++ Seq(
    typescript in Assets := (typescript in Assets).value,
    typescript in TestAssets := (typescript in TestAssets).value
  )

}
