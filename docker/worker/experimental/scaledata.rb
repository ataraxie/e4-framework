results_dir = ENV['E4_RESULTS_DIR']

def median(array)
  sorted = array.sort
  len = sorted.length
  (sorted[(len - 1) / 2] + sorted[len / 2]) / 2.0
end

appkey="lb"
envs=["n1-u50","n1-u150","n1-u250","n2-u50","n2-u150","n2-u250","n4-u50","n4-u150","n4-u250"]
urls = [
"/rest/lively/blog/latest/navigation/posts/popular",
"/rest/lively/blog/latest/navigation/categories",
"/rest/lively/blog/latest/publish-as-blog-post/publish",
"/rest/lively/blog/latest/blogposts/macro",
"/rest/lively/blog/latest/blogposts/dashboard",
"/rest/lively/blog/latest/blogposts/overview/featured",
"/rest/lively/blog/latest/blogposts/overview/category",
"/rest/lively/blog/latest/blogposts/overview/search"
]
results = {}
envs.each do |env|
  puts "Creating results for env: #{env}"
  first_numbers = []
  urls.each do |url|
    
    grep = `grep #{url.gsub("/","\/")} #{results_dir}/#{appkey}-#{env}/access-log-*.log`
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
