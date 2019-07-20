results_dir = ENV['E4_RESULTS_DIR']
statement_key=ENV['E4_STATEMENT_KEY']

if !results_dir || !statement_key || !["nodespread", "vanilla_times_taken"].include?(statement_key)
  puts "Env variables E4_RESULTS_DIR and E4_STATEMENT_KEY must be set"
  exit
end

appkey = "vn"
envs = ["n1-u50","n1-u150","n1-u250","n2-u50","n2-u150","n2-u250","n4-u50","n4-u150","n4-u250"]

statements = {}
statements['vanilla_times_taken'] = "SELECT CAST(ROUND(AVG(time_taken), 0) as int) as average_time_taken FROM E4Measurement WHERE virtual_user IN ('Commentor','Creator','Dashboarder','Editor','Reader','Searcher') GROUP BY virtual_user ORDER BY virtual_user;"
statements['nodespread'] = "SELECT COUNT(*) FROM E4Measurement GROUP BY node_id"

results = []
envs.each do |env|
  sqlite_file = "#{results_dir}/#{appkey}-#{env}/e4.sqlite"
  puts "Looking at file #{sqlite_file}"
  result = `sqlite3 -list #{sqlite_file} "#{statements[statement_key]}"`
  result = result.split("\n")
  puts result
  if statement_key == "vanilla_times_taken"
    result.each_with_index do |number, index|
      results[index] = [] unless results[index]
      results[index].push(number)
    end
  else
    4.times do |index|
      results[index] = [] unless results[index]
      results[index].push(result[index] ? result[index] : "")
    end
  end
end

puts
puts "=== RESULT FOR SPREADSHEET ==="
results.each do |line|
  puts line.join(",")
end