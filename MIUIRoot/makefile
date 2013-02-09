
all: JLB10ROOT.zip 
SRC=system/xbin/su system/app/LBESEC_MIUI.apk META-INF/com/google/android/updater-script

JLB10ROOT.zip: $(SRC)
	zip -r $@ META-INF system

clean:
	rm -f JLB*.zip
