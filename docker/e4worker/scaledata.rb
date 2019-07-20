def median(array)
  sorted = array.sort
  len = sorted.length
  (sorted[(len - 1) / 2] + sorted[len / 2]) / 2.0
end

appkey="lt"
envs=["n1-u50","n1-u150","n1-u250","n2-u50","n2-u150","n2-u250","n4-u50","n4-u150","n4-u250"]
urls = [
"/rest/experimental/relation/user/current/favourite/toSpace",
"/rest/experimental/relation/user/current/favourite/toContent",
"/rest/lively-theme/latest/custom-element/createPage",
"/rest/lively-theme/latest/custom-element/state"
]
results = {}
envs.each do |env|
  puts "Creating results for env: #{env}"
  first_numbers = []
  urls.each do |url|
    
    grep = `grep #{url.gsub("/","\/")} #{appkey}-#{env}/access-log-rest.log` 
    grep.gsub!(/\|.*/,"")
    numbers = grep.split("\n")
    numbers.map!(&:to_i)
    first_numbers.push(numbers[0])
    med = median(numbers).to_i
    if !results[url]
      results[url] = []
    end
    results[url].push(med)
  end
  puts "Verify no duplicates: " + first_numbers.join(",")
end

results.each do |url, arr|
  puts url + "," + arr.join(",")
end
