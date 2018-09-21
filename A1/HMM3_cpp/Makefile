APP=HMM3

SRC_DIR=.
INC_DIR=../include
BUILD_DIR=build

CC=g++
LD=g++
CFLAGS=-O2 -c -Wall -std=c++11
LFLGAS=-std=c++11
DFLAGS=-g3 -O0 -DDEBUG
INCFLAGS=-I$(INC_DIR) -I$(SRC_DIR)

SOURCES=$(wildcard $(SRC_DIR)/*.cpp $(INC_DIR)/*.cpp)
HEADERS=$(wildcard $(INC_DIR)/*.h $(INC_DIR)/.h)
OBJECTS=$(SOURCES:$(SRC_DIR)/%.cpp:$(SRC_DIR)/*.cpp=$(BUILD_DIR)/%.o)
DEPENDS=$(BUILD_DIR)/.depends


.PHONY: all 
all: $(BUILD_DIR)/$(APP)

.PHONY: debug
debug: CFLAGS+=$(DFLAGS)
debug: all 


$(BUILD_DIR)/$(APP): $(OBJECTS) | $(BUILD_DIR)
	$(LD) $(LFLGAS) $(INCFLAGS) -o $@ $^

$(BUILD_DIR)/%.o: | $(BUILD_DIR)
	$(CC) $(CFLAGS) $(INCFLAGS) -o $@ $<

$(DEPENDS): $(SOURCES) | $(BUILD_DIR)
	$(CC) $(INCFLAGS) -MM $(SOURCES) | sed -e 's!^!$(BUILD_DIR)/!' >$@

ifneq ($(MAKECMDGOALS),clean)
-include $(DEPENDS)
endif


$(BUILD_DIR):
	mkdir -p $@

.PHONY: clean
clean:
	rm -rf $(BUILD_DIR)