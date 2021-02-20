require "json"

json = JSON.parse(File.read("target_file"))

if json["error"] then
  File.open("exit_message", mode = "w") {|f|
    f.write(json["error"])
  }
  exit
end

File.open("dependencies/ext.gradle", mode = "w") {|f|
  f.write("ext {\n")
  f.write("  appVersionCode = ")
  f.write(json["code"])
  f.write("\n")
  f.write("  appVersionName = '")
  f.write(json["name"])
  f.write("'\n")
  f.write("  adMobCode = '")
  f.write(ARGV[0])
  f.write("'\n")
  f.write("  adMobBannerKey = '")
  f.write(ARGV[1])
  f.write("'\n")
  f.write("  inquiryUrl = '")
  f.write(ARGV[2])
  f.write("'\n")
  f.write("  stagingDomain = '")
  f.write(ARGV[3])
  f.write("'\n")
  f.write("  releaseDomain = '")
  f.write(ARGV[4])
  f.write("'\n")
  f.write("}")
}

File.open("fastlane/metadata/android/en-US/changelogs/" + json["code"].to_s + ".txt", mode = "w") {|f|
  f.write(json["english"])
}
File.open("fastlane/metadata/android/ja-JP/changelogs/" + json["code"].to_s + ".txt", mode = "w") {|f|
  f.write(json["japanese"])
}
