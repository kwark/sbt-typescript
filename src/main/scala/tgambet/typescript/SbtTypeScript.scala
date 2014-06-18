package tgambet.typescript

import com.typesafe.sbt.jse.SbtJsTask
import com.typesafe.sbt.web.Import._
import me.fornever.ResourceHelper
import sbt.Keys._
import sbt._
import spray.json.{JsBoolean, JsObject, JsString}

object Import {

  object TypeScriptKeys {

    val typescript = TaskKey[Seq[File]]("typescript", "Invoke the TypeScript compiler.")

    val modules = SettingKey[Seq[String]]("typescript-modules", "A list of file names to be exported as modules (using TypeScript --out parameter).")

    val targetES5 = SettingKey[Boolean]("typescript-opt-target", "Whether TypeScript should target ECMAScript 5. False by default (ES3).")

    val sourceMap = SettingKey[Boolean]("typescript-opt-sourceMap", "Whether TypeScript should generate corresponding .map files.")

    object ModuleType extends Enumeration {
      val Amd, CommonJs = Value
    }

    val moduleType = SettingKey[ModuleType.Value]("typescript-opt-module", "Specify module code generation: 'Commonjs' or 'Amd'")

    val noImplicitAny = SettingKey[Boolean]("typescript-opt-noImplicitAny", "Warn on expressions and declarations with an implied 'any' type.")

    val removeComments = SettingKey[Boolean]("typescript-opt-removeComments", "Do not emit comments to output.")

  }

}

object SbtTypeScript extends AutoPlugin {

  override def requires = SbtJsTask
  override def trigger = AllRequirements
  val autoImport = Import

  import com.typesafe.sbt.jse.SbtJsTask.autoImport.JsTaskKeys._
  import tgambet.typescript.SbtTypeScript.autoImport.TypeScriptKeys._

  val defaults = Seq(
    modules := Seq.empty,
    targetES5 := false,
    sourceMap := false,
    moduleType := ModuleType.Amd,
    noImplicitAny := false,
    removeComments := false
  )

  val typeScriptSettings = Seq(

    includeFilter := "*.ts",

    excludeFilter := "*.d.ts",

    jsOptions := JsObject(
      "outDir" -> JsString((resourceManaged in typescript in Assets).value.getAbsolutePath),
      "targetES5" -> JsBoolean((targetES5 in Assets).value),
      "sourceMap" -> JsBoolean((sourceMap in Assets).value),
      "noImplicitAny" -> JsBoolean((noImplicitAny in Assets).value),
      "removeComments" -> JsBoolean((removeComments in Assets).value),
      "module" -> JsString((moduleType in Assets).value match {
        case ModuleType.Amd => "amd"
        case ModuleType.CommonJs => "commonjs"
      })
    ).toString()
  )

  override def projectSettings = defaults ++ inTask(typescript)(
    SbtJsTask.jsTaskSpecificUnscopedSettings ++ Seq(
      shellSource := {
        ResourceHelper.wrapTypescriptModuleAndCopyTo(
          (target in Plugin).value / moduleName.value,
          getClass.getClassLoader.getResource("typescript/tsc.js"),
          streams.value.cacheDirectory / "copy-resource"
        )

        ResourceHelper.copyResourceTo(
          (target in Plugin).value / moduleName.value,
          shellFile.value,
          streams.value.cacheDirectory / "copy-resource"
        )
      }
    ) ++
    inConfig(Assets)(typeScriptSettings) ++
    inConfig(TestAssets)(typeScriptSettings) ++
    Seq(
      moduleName := "typescript",
      shellFile := getClass.getClassLoader.getResource("typescript/sbt-typescript.js"),

      taskMessage in Assets := "TypeScript compiling",
      taskMessage in TestAssets := "TypeScript test compiling"
    )
  ) ++ SbtJsTask.addJsSourceFileTasks(typescript) ++ Seq(
    typescript in Assets := (typescript in Assets).value,
    typescript in TestAssets := (typescript in TestAssets).value
  )

}
