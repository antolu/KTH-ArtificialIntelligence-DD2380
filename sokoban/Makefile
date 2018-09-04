APP=sokoban

SRC_DIR=.
INC_DIR=inc
BUILD_DIR=build

CC=g++
LD=g++
CFLAGS=-O2 -c -Wall -std=c++11
LFLGAS=
DFLAGS=-g3 -O0 -DDEBUG
INCFLAGS=-I$(INC_DIR)

SOURCES=$(wildcard $(SRC_DIR)/*.cpp)
HEADERS=$(wildcard $(INC_DIR)/*.hpp)
OBJECTS=$(SOURCES:$(SRC_DIR)/%.cpp=$(BUILD_DIR)/%.o)
DEPENDS=$(BUILD_DIR)/.depends


.PHONY: all 
all: $(BUILD_DIR)/$(APP)

.PHONY: debug
debug: CFLAGS+=$(DFLAGS)
debug: all 


$(BUILD_DIR)/$(APP): $(OBJECTS) | $(BUILD_DIR)
	$(LD) $(LFLGAS) -o $@ $^

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