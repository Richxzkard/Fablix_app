def log_processing(filename):
    # Use a breakpoint in the code line below to debug your script.
    f = open(filename, "r")

    count = 0
    total_servlet = 0
    total_JDBC = 0
    for line in f:
        entry = line.split(" ")
        TS, TJ = float(entry[0]), float(entry[1])
        TS /= 1000000000
        TJ /= 1000000000

        total_servlet += TS
        total_JDBC += TJ
        count += 1

    average_servlet = total_servlet / count
    average_JDBC = total_JDBC / count
    print("Average servlet response time: " + str(average_servlet))
    print("Average JDBC response time: " + str(average_JDBC))





# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    log_processing(input())

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
