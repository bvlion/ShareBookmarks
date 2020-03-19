require "json"
require "uri"
require "net/http"

uri = URI.parse(ARGV[0])
redirect_url = Net::HTTP.get_response(uri)["location"]
response = Net::HTTP.get_response(URI.parse(redirect_url))
json = JSON.parse(response.body)

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
  f.write(ARGV[1])
  f.write("'\n")
  f.write("}")
}

File.open("fastlane/metadata/android/en-US/changelogs/" + json["code"] + ".txt", mode = "w") {|f|
  f.write(json["english"])
}
File.open("fastlane/metadata/android/ja-JP/changelogs/" + json["code"] + ".txt", mode = "w") {|f|
  f.write(json["japanese"])
}
